plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(":core"))
    implementation(project(":components"))
    implementation(project(":playground"))
    testImplementation(libs.gson)
}

tasks {
    register<JavaExec>("FxDocs") {
        group = "apps"
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("com.docs.DocsApp")
        args = listOf(
                //Root for all variants (Tree Root in the app)
                "${rootDir}/docs/src/main/java/com/docs",
                //Root for playground (Tree Root in the app)
                "${rootDir}/playground/src/main/java",
                //File to persist last app dimensions, location, selection, etc
                "${rootDir}/docs/src/main/playground.json"
        )
        enableAssertions = true
    }
}