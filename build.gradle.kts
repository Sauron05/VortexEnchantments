plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

// ProGuard configuration
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.6.1")
    }
}

group = "com.vortexrpg"
version = "1.1.0"
description = "VortexEnchantments - custom enchantments for Paper/Folia 1.21.11 -> 26.1.2"

val pluginVersion = version.toString()

// ─── Cross-version strategy ──────────────────────────────────────────────────
// One universal jar runs on BOTH Minecraft 1.21.11 and 26.1.2 (Tiny Takeover):
//   * Compile against the lowest supported API (1.21.11). The Bukkit/Paper API is
//     forward-compatible, so the same artifact links cleanly on 26.1.2.
//   * Emit Java 21 bytecode (release 21). It loads on the Java 21 runtime required
//     by 1.21.11 AND the Java 25 runtime required by 26.1.x.
//   * plugin.yml keeps api-version '1.21' (accepted by every server >= 1.21).
// The same jar also runs on a Fabric server via the Cardboard compatibility mod
// (see README.md → "Running on Fabric").
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("org.bstats", "com.vortexrpg.enchantments.libs.bstats")
        minimize {
            exclude(dependency("org.bstats:.*"))
        }
    }

    build {
        dependsOn(shadowJar)
    }

    // ProGuard obfuscation task
    register<proguard.gradle.ProGuardTask>("proguard") {
        dependsOn(shadowJar)

        val shadowOutput = shadowJar.get().archiveFile.get().asFile
        injars(shadowOutput)
        outjars(layout.buildDirectory.file("libs/${project.name}-${project.version}-obfuscated.jar"))

        // JDK 21 runtime for ProGuard library reference (JDK 25 jmods too new for ProGuard 7.x)
        val jdk21 = "C:/Program Files/Eclipse Adoptium/jdk-21.0.10.7-hotspot"
        libraryjars(mapOf("jarfilter" to "!**.jar", "filter" to "!module-info.class"),
            "$jdk21/jmods/java.base.jmod")
        libraryjars(mapOf("jarfilter" to "!**.jar", "filter" to "!module-info.class"),
            "$jdk21/jmods/java.logging.jmod")

        // Use the compile classpath (Paper API, ProtocolLib, etc.) as library
        configurations.compileClasspath.get().forEach { libraryjars(it) }

        // Keep the main plugin class
        keep("public class com.vortexrpg.enchantments.VortexEnchantments extends org.bukkit.plugin.java.JavaPlugin { *; }")

        // Keep all classes Bukkit discovers via reflection
        keep("public class * implements org.bukkit.event.Listener { *; }")
        keep("public class * extends org.bukkit.command.Command { *; }")
        keep("public class * implements org.bukkit.command.CommandExecutor { *; }")
        keep("public class * implements org.bukkit.command.TabCompleter { *; }")

        // Keep PlaceholderAPI expansion
        keep("public class * extends me.clip.placeholderapi.expansion.PlaceholderExpansion { *; }")

        // Keep bStats (relocated)
        keep("class com.vortexrpg.enchantments.libs.bstats.** { *; }")

        // Keep VortexEnchant subclasses (registered by name/reflection in EnchantManager)
        keep("public class * extends com.vortexrpg.enchantments.enchant.VortexEnchant { *; }")

        // Keep enums (used in config parsing)
        keepclassmembers("enum * { public static **[] values(); public static ** valueOf(java.lang.String); }")

        // Keep API class for external plugins
        keep("public class com.vortexrpg.enchantments.api.** { *; }")

        // Obfuscation settings
        obfuscationdictionary("proguard-dict.txt")
        classobfuscationdictionary("proguard-dict.txt")
        packageobfuscationdictionary("proguard-dict.txt")

        repackageclasses("v")
        allowaccessmodification()
        overloadaggressively()

        // Don't optimize or shrink — just obfuscate
        dontshrink()
        dontoptimize()

        // Suppress warnings for Paper internals
        dontwarn()
    }

    processResources {
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            filter<org.apache.tools.ant.filters.ReplaceTokens>(
                "tokens" to mapOf("project.version" to pluginVersion),
                "beginToken" to "\${",
                "endToken" to "}"
            )
        }
    }
}
