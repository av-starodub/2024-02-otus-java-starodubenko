rootProject.name = "otusJavaHomework"
include("HW01-gradle")
include("HW01-gradle")
include("HW02-collections")
include("HW03-annotations")
include("HW04-gc")
include("HW05-aop")
include("HW07-solid")
include("HW09-jdbc:demo")
include("HW09-jdbc:homework")
include("HW10-jdbc:demo")
include("HW10-jdbc:homework")
include("HW03-stream_api")
include("HW04-concurrency")
include("HW05-patterns-1v")
include("HW05-patterns-2")
include("HW07-jdbc_advanced")
include("HW13-JPQL:homework-template")
include("HW12-JPA")
include("HW11-spring_boot_bsse")
include("HW10-spring_context")
include("HW14-spring_data_jdbc")

pluginManagement {
    val jgitver: String by settings
    val dependencyManagement: String by settings
    val springframeworkBoot: String by settings
    val johnrengelmanShadow: String by settings
    val jib: String by settings
    val protobufVer: String by settings
    val sonarlint: String by settings
    val spotless: String by settings

    plugins {
        id("fr.brouillard.oss.gradle.jgitver") version jgitver
        id("io.spring.dependency-management") version dependencyManagement
        id("org.springframework.boot") version springframeworkBoot
        id("com.github.johnrengelman.shadow") version johnrengelmanShadow
        id("com.google.cloud.tools.jib") version jib
        id("com.google.protobuf") version protobufVer
        id("name.remal.sonarlint") version sonarlint
        id("com.diffplug.spotless") version spotless
    }
}

include("HW09")
include("HW09-jdbc")