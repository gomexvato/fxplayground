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
