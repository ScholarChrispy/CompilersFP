plugins {
    antlr
    kotlin("jvm") version "1.9.23"
    application
}

group = "org.CFP"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    antlr("org.antlr:antlr4:4.11.1")
}

kotlin {
    jvmToolchain(21)
}

tasks.generateGrammarSource {
    outputDirectory = file("build/generated/sources/main/kotlin/antlr/grammar")

    // set the package of the generated ANTLR code to PL
    arguments = listOf("-package", "PL")
}

sourceSets {
    main {
        java {
            srcDir(tasks.generateGrammarSource)
        }
    }
}

application {
    mainClass = "backend.MainKt"
}
