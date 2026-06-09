plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "com.vortexrpg"
version = "1.1.0"
description = "VortexEnchantments - custom enchantments for Paper and Folia"

val pluginVersion = version.toString()

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
