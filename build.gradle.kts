import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("root-plugin")
}

tasks {
    assemble {
        val jarsDir = File("$rootDir/jars")

        doFirst {
            delete(jarsDir)

            jarsDir.mkdirs()
        }

        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")

            doLast {
                runCatching {
                    if (project.name != "api") {
                        val file = File("$jarsDir/${project.name.uppercaseFirstChar()}")

                        file.mkdirs()

                        copy {
                            from(project.layout.buildDirectory.file("libs/${rootProject.name}-${project.version}.jar"))
                            into(file)
                        }
                    }
                }.onSuccess {
                    // Delete to save space on jenkins.
                    delete(project.layout.buildDirectory.get())
                    delete(rootProject.layout.buildDirectory.get())
                }.onFailure {
                    println("Failed to copy file out of build folder into jars directory: Likely does not exist.")
                }
            }
        }
    }
}