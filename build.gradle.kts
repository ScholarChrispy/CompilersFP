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
    // set output directory to some arbitrary location in `/build` directory.
    // by convention `/build/generated/sources/main/java/<generator name>` is often used
    outputDirectory = file("build/generated/sources/main/kotlin/antlr/grammar")

    // pass -package to make generator put code in not default space
    arguments = listOf("-package", "PL")
}

sourceSets {
    main {
        java {
            // telling that output generateGrammarSource should be part of main source set
            // actually passed value will be equal to `outputDirectory` that we configured above
            srcDir(tasks.generateGrammarSource)
        }
    }
}

application {
    mainClass = "backend.MainKt"
}
