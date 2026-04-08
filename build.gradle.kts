plugins {
    java
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.testfx:testfx-core:4.0.18")
    testImplementation("org.testfx:testfx-junit:4.0.18")
}

application {
    mainClass.set("com.example.MainApp")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.MainApp"
    }
}
