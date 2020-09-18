import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val protobufVersion = "3.13.0"
val grpcVersion = "1.31.1"
val grpcKotlinVersion = "0.1.5"
val kotestVersion = "4.2.5"

plugins {
    application
    kotlin("jvm") version "1.4.10"
    id("com.google.protobuf") version "0.8.13"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("com.google.cloud.tools.jib") version "2.5.0"
}

jib.from.image = "https://registry.hub.docker.com://openjdk:14-alpine"

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:9.4.0")
    }
}
apply(plugin = "org.jlleitschuh.gradle.ktlint")

group = "com.nedellis"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    google()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    implementation("com.linecorp.armeria:armeria:1.0.0")
    implementation("com.linecorp.armeria:armeria-grpc:1.0.0")
    implementation("com.linecorp.armeria:armeria-kotlin:1.0.0")

    runtimeOnly("org.slf4j:slf4j-simple:1.7.30")
    runtimeOnly("org.slf4j:slf4j-api:1.7.30")

    implementation("com.google.protobuf:protobuf-java-util:$protobufVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")

    implementation("redis.clients:jedis:3.3.0")

    implementation("com.typesafe:config:1.4.0")

    testImplementation("io.mockk:mockk:1.10.0")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property:$kotestVersion") // for kotest property test
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "com.nedellis.azalea.MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xjsr305=strict",
            "-java-parameters"
        )
        jvmTarget = "1.8"
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main")
            srcDirs("build/generated/source/proto/main")
        }
    }
}

ktlint {
    filter {
        exclude { element -> element.file.path.contains("generated") }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
