plugins {
    java
    id("org.springframework.boot") version "3.5.8"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "srangeldev"
version = "0.0.1-SNAPSHOT"
description = "CentroCocotero"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    //Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Pebble Template Engine
    implementation("io.pebbletemplates:pebble-spring-boot-starter:3.2.4")
    implementation("io.pebbletemplates:pebble:3.2.4")

    // BBDD
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    // Spring Boot DevTools
    //developmentOnly("org.springframework.boot:spring-boot-devtools")

    // PDF Generation
    implementation("com.itextpdf:itextpdf:5.5.13.4")
    implementation("com.itextpdf:html2pdf:5.0.5")

    // Image processing
    implementation("org.imgscalr:imgscalr-lib:4.2")

    //Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.pebbletemplates:pebble-spring-boot-starter:3.2.2")
    implementation("io.pebbletemplates:pebble:3.2.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
