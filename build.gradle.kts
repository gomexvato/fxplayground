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

            tasks.register("coverageSummary") {
                group = "verification"
                description = "Prints a JaCoCo coverage summary to the console for this module."
                dependsOn("jacocoTestReport")
                doLast {
                    val xml = layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile
                    if (!xml.exists()) {
                        println("No coverage data for :${project.name}. Run tests first.")
                        return@doLast
                    }

                    data class Counter(val type: String, var covered: Int = 0, var missed: Int = 0) {
                        val total get() = covered + missed
                        val pct get() = if (total == 0) 0.0 else covered * 100.0 / total
                    }

                    val counters = mutableMapOf<String, Counter>()
                    val factory = javax.xml.parsers.SAXParserFactory.newInstance().apply {
                        setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                        setFeature("http://xml.org/sax/features/validation", false)
                    }
                    factory.newSAXParser().parse(xml, object : org.xml.sax.helpers.DefaultHandler() {
                        override fun startElement(uri: String, local: String, name: String, attrs: org.xml.sax.Attributes) {
                            if (name == "counter" && attrs.getValue("type") != null) {
                                val type = attrs.getValue("type")
                                val c = counters.getOrPut(type) { Counter(type) }
                                c.covered += attrs.getValue("covered").toInt()
                                c.missed  += attrs.getValue("missed").toInt()
                            }
                        }
                    })

                    val order = listOf("INSTRUCTION", "BRANCH", "LINE", "METHOD", "CLASS")
                    val rows = order.mapNotNull { counters[it] }

                    println("\n┌─────────────────────────────────────────────────────┐")
                    println("│  Coverage Summary: :${project.name.padEnd(32)}│")
                    println("├──────────────┬──────────┬──────────┬──────────────────┤")
                    println("│ Type         │  Covered │   Missed │          Total   │")
                    println("├──────────────┼──────────┼──────────┼──────────────────┤")
                    rows.forEach { c ->
                        println("│ ${c.type.padEnd(12)} │ ${c.covered.toString().padStart(8)} │ ${c.missed.toString().padStart(8)} │ ${c.total.toString().padStart(6)} (${"%.1f".format(c.pct).padStart(5)}%) │")
                    }
                    println("└──────────────┴──────────┴──────────┴──────────────────┘\n")
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
