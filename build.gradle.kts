import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "cf.lucasmellof"
version = "0.9.0"

plugins {
    java
    application
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    }
}

application {
    mainClassName = "${group}.senior.Music"
}


defaultTasks("run")

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://jitpack.io")
}

dependencies {
    //[KOTLIN]
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    //[DISCORD]
    implementation("net.dv8tion:JDA:4.2.0_186")
    implementation("com.github.devoxin:flight:2.0.8")

    //[LOG]
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    //[DATABASE]
    implementation("org.litote.kmongo:kmongo:4.0.3")

    //[AUDIO]
    implementation("com.sedmelluq:lavaplayer:1.3.50")

    //[LIBRARY]
    implementation("commons-io:commons-io:2.7")
    implementation("org.apache.commons:commons-lang3:3.8.1")
    implementation("com.google.code.gson:gson:2.8.6")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}