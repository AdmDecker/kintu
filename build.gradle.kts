plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
    id("org.jetbrains.kotlin.kapt") version "1.8.22"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.22"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.graalvm.buildtools.native") version "0.9.23"

    kotlin("plugin.serialization") version "1.8.22"
}

version = "0.1"
group = "kintu"

val kotestVersion="5.6.2"
val kafkaVersion="3.5.0"
val javaVersion="19"
val picocliVerson="4.7.4"

val kotlinVersion= project.properties["kotlinVersion"]
repositories {
    mavenCentral()
}

dependencies {
    kapt("info.picocli:picocli-codegen:$picocliVerson")
    implementation("info.picocli:picocli:$picocliVerson")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("com.sksamuel.hoplite:hoplite-core:2.7.4")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.7.4")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    compileOnly("org.graalvm.nativeimage:svm:23.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    testImplementation("com.google.jimfs:jimfs:1.2")
    testImplementation("io.mockk:mockk:1.13.5")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("org.apache.kafka:kafka-streams:$kafkaVersion")
    implementation("org.apache.kafka:connect-runtime:$kafkaVersion")
    implementation("com.jayway.jsonpath:json-path:2.8.0")
}

java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = javaVersion
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = javaVersion
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "kintu.KintuCommand"
    }
}