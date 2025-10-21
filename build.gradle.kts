plugins {
    kotlin("jvm") version "2.2.10"
    application
}

group = "com.plaglefleau"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.plaglefleau.clashofclansmanage.Main")
}

dependencies {
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.13")

    // Let logback pull slf4j-api 2.0.17 transitively
    runtimeOnly("ch.qos.logback:logback-classic:1.5.13")

    // Retrofit: use 2.x (3.0.0 doesn’t exist)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")

    implementation("org.postgresql:postgresql:42.7.8")
    implementation("net.dv8tion:JDA:6.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

kotlin {
    jvmToolchain(21)
}