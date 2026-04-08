subprojects {
    group = "com.example"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    apply(plugin = "jacoco")

    afterEvaluate {
        val hasTests = configurations.findByName("testRuntimeClasspath") != null
        if (hasTests) {
            tasks.withType<Test> {
                finalizedBy(tasks.named("jacocoTestReport"))
            }

            tasks.named<JacocoReport>("jacocoTestReport") {
                reports {
                    xml.required.set(true)
                    html.required.set(true)
                    csv.required.set(false)
                }
            }
        }
    }
}

tasks.register("coverageReport") {
    group = "verification"
    description = "Runs tests and generates JaCoCo coverage reports for all modules."
    dependsOn(subprojects.map { it.tasks.matching { t -> t.name == "jacocoTestReport" } })
}

tasks.register("openCoverageReport") {
    group = "verification"
    description = "Opens JaCoCo HTML coverage reports for all modules that have one."
    doLast {
        val reports = subprojects.mapNotNull { sub ->
            sub.layout.buildDirectory.file("reports/jacoco/test/html/index.html")
                .get().asFile
                .takeIf { it.exists() }
        }
        if (reports.isEmpty()) {
            println("No coverage reports found. Run './gradlew test coverageReport' first.")
        } else {
            reports.forEach { java.awt.Desktop.getDesktop().browse(it.toURI()) }
        }
    }
}
