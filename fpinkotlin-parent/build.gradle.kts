ext["kotlintestVersion"] = "3.3.2"
ext["logbackVersion"] = "1.2.3"
ext["slf4jVersion"] = "1.7.25"
ext["junitVersion"] = "3.3.2"

plugins {
    base
    kotlin("jvm") version "1.3.31"
    id("com.github.ben-manes.versions") version "0.21.0"
}


allprojects {

    group = "com.fpinkotlin"

    version = "1.0-SNAPSHOT"

    repositories {
        jcenter()
        mavenCentral()
    }
}
