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
version = "1.0.1"
description = "G-Wasm"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        groupId = "dorving.gearth"
        artifactId = "gwasm"
        version = "1.0.1"
        from(components["java"])
    }
}
