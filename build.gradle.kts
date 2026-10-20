plugins {
    kotlin("jvm") version "2.4.10"
    application
    jacoco
    id("org.jmailen.kotlinter") version "5.7.0"
}

application {
    mainClass.set("martianrobots.MainKt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.forkhandles:result4k:3.1.0.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}

testing {
    suites {
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation("dev.forkhandles:result4k:3.1.0.0")
                implementation("org.jetbrains.kotlin:kotlin-test-junit5")
            }
            targets.all {
                testTask.configure { shouldRunAfter(tasks.test) }
            }
        }
    }
}

// Combined coverage report for unit and integration tests
tasks.jacocoTestReport {
    val integrationTest = tasks.named<Test>("integrationTest")
    dependsOn(tasks.test, integrationTest)
    executionData(tasks.test.get(), integrationTest.get())
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.jacocoTestCoverageVerification {
    val integrationTest = tasks.named<Test>("integrationTest")
    dependsOn(tasks.test, integrationTest)
    executionData(tasks.test.get(), integrationTest.get())
    violationRules {
        rule {
            limit {
                counter = "LINE"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(testing.suites.named("integrationTest"))
    dependsOn(tasks.jacocoTestReport)
    dependsOn(tasks.jacocoTestCoverageVerification)
}