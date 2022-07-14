plugins {
    java
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

group = "G-Earth"
version = "1.0.3"
description = "G-Wasm-Minimal"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        groupId = "dorving.gearth"
        artifactId = "wasm-minimal"
        version = "1.0.3"
        from(components["java"])
    }
}
