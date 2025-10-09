plugins {
    kotlin("jvm") version "2.2.10"
}

group = "com.plaglefleau"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    //implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("org.postgresql:postgresql:42.7.8")
    implementation("net.dv8tion:JDA:6.0.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}