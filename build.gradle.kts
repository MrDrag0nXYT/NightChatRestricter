plugins {
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "zxc.mrdrag0nxyt"
version = "1.1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")

//    compileOnly("com.zaxxer:HikariCP:6.2.0")
//    compileOnly("org.xerial:sqlite-jdbc:3.47.0.0")

    implementation("com.zaxxer:HikariCP:6.2.0")
    implementation("org.xerial:sqlite-jdbc:3.47.0.0")

    implementation("org.bstats:bstats-bukkit:3.0.2")
}

val targetJavaVersion = 17
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    relocate("org.bstats", "zxc.mrdrag0nxyt.org.bstats")
}
