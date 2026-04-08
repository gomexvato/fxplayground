plugins {
    java
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.testfx.core)
    testImplementation(libs.testfx.junit)
    testImplementation(libs.testfx.monocle)
}

application {
    mainClass.set("com.example.MainApp")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.MainApp"
    }
}

val headlessJvmArgs = listOf(
    "-Djava.awt.headless=true",
    "-Dtestfx.robot=glass",
    "-Dtestfx.headless=true",
    "-Dprism.order=sw",
    "-Dprism.text=t2k"
)

tasks.test {
    jvmArgs(headlessJvmArgs)
}

tasks.register<Test>("testHeadful") {
    description = "Runs tests with a real display (requires DISPLAY to be set on Linux/WSL)."
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
}
