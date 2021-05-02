import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.4.30-M1"
    kotlin("plugin.serialization") version "1.4.30-M1"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    application
}

group = "tlp.media.server.komga"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
}

val libDir = projectDir.absolutePath + File.separator +  "lib"
println("Local library: $libDir" )

val ktorVersion = "1.4.0"
dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("io.github.microutils:kotlin-logging:1.12.0")
    implementation("com.github.ben-manes.caffeine:caffeine:2.8.6")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation("me.tongfei:progressbar:0.9.0")
    implementation("com.github.tachiyomiorg:extensions-lib:a596412")

    testImplementation("com.squareup.okhttp3:okhttp:3.10.0")
    testImplementation("com.google.code.gson:gson:2.8.2")
    testImplementation("io.reactivex:rxjava:1.3.6")
    testImplementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation(files("${libDir}${File.separator}android.jar"))
}


application {
    mainClassName = "tlp.media.server.komga.ApplicationKt"
}

tasks.withType<KotlinCompile> {
    val java8 = JavaVersion.VERSION_1_8.toString()
    sourceCompatibility = java8
    targetCompatibility = java8
    kotlinOptions.jvmTarget = java8
}


tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "tlp.media.server.komga.ApplicationKt"
            )
        )
    }
}
