plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.serialization") version "2.4.20"
    application
}

group = "de.hs-flensburg.brandon-hoffmann"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation:3.6.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.6.0")
    implementation("io.ktor:ktor-client-cio:3.6.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.6.0")
    testImplementation("io.ktor:ktor-server-test-host:3.6.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("io.ktor:ktor-server-core:3.6.0")
    implementation("io.ktor:ktor-server-netty:3.6.0")
    implementation("io.ktor:ktor-server-html-builder:3.6.0")
    implementation("io.ktor:ktor-server-call-logging:3.6.0")
    implementation("ch.qos.logback:logback-classic:1.5.11")

    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("ApplicationKt")
}
