plugins {
    // Loom 1.14+ split plugin: "-remap" variant is for obfuscated MC (<= 1.21.11, Yarn).
    id("net.fabricmc.fabric-loom-remap") version "1.14.10"
}

base { archivesName.set(project.property("archives_base_name") as String) }
version = project.property("mod_version") as String
group = project.property("maven_group") as String

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}
