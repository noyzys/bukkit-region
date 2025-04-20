plugins {
    kotlin("jvm") version "2.1.20"
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.nautchkafe.region"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/public/") 
}

dependencies {
    // Spigot API
    implementation("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")

    // Test stuff
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.mockito:mockito-core:5.1.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}

tasks {
    shadowJar {
        archiveBaseName.set("region-plugin")
        archiveClassifier.set("")
        archiveVersion.set(version)
    }
    
    run {
        jvmArgs = listOf("-Xmx1024m", "-Xms1024m") 
    }
}