@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.web3j.gradle.plugin.GenerateContractWrappers

val kotlin_version: String by project
val kotlin_coroutines_version: String by project
val typesafe_config_version: String by project
val logback_version: String by project
val web3j_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("org.web3j") version "4.9.4"
}

group = "io.github.rorione"
version = "0.0.1"

application {
    mainClass.set("io.github.rorione.ApplicationKt")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://hyperledger.jfrog.io/hyperledger/besu-maven")
        content { includeGroupByRegex("org\\.hyperledger\\..*") }
    }
    maven {
        url = uri("https://artifacts.consensys.net/public/maven/maven/")
        content { includeGroupByRegex("tech\\.pegasys\\..*") }
    }
    maven {
        url = uri("https://dl.cloudsmith.io/public/consensys/quorum-mainnet-launcher/maven/")
        content { includeGroupByRegex("net\\.consensys\\..*") }
    }
    maven {
        url = uri("https://splunk.jfrog.io/splunk/ext-releases-local")
        content { includeGroupByRegex("com\\.splunk\\..*") }
    }
    @Suppress("DEPRECATION")
    jcenter()
}

dependencies {
    // Config
    implementation("com.typesafe:config:$typesafe_config_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Web3j
    implementation("org.web3j:core:$web3j_version")

    // Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
    testImplementation("org.web3j:web3j-unit:$web3j_version")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val abiGenPath = "build/generated/sources/src/abi/main/java"

java.sourceSets["main"].java.srcDir(abiGenPath)

task("generate-abi").doLast {
    val abiSources = File(projectDir, "src/main/resources/abi")
    val generatedSources = File(projectDir, abiGenPath)
    val generatedPackage = "io.github.rorione.abi"

    abiSources
        .listFiles()
        .orEmpty()
        .filter { it.extension == "json" && it.nameWithoutExtension.endsWith("abi") }
        .forEach {
            org.web3j.codegen.SolidityFunctionWrapperGenerator(
                null,
                it,
                generatedSources,
                it.nameWithoutExtension
                    .dropLast(4)
                    .split('_')
                    .joinToString("", transform = String::capitalize),
                generatedPackage,
                true,
                false,
                20
            ).generate()
        }
}

tasks.withType<GenerateContractWrappers>() {
    dependsOn("generate-abi")
}

tasks.withType<KotlinCompile>() {
    dependsOn("generate-abi")
}
