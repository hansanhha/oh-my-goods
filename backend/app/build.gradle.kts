import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    application
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation(libs.spring.boot)
    implementation(libs.spring.web)
    implementation(libs.spring.validation)
    implementation(libs.spring.data.jpa)
    implementation(libs.spring.security)
    implementation(libs.spring.oauth2.client)
    implementation(libs.spring.springdoc)

    implementation("com.querydsl:querydsl-jpa:${libs.versions.querydsl.get()}:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:${libs.versions.querydsl.get()}:jakarta")
    annotationProcessor(libs.jakarta.annotation.api)
    annotationProcessor(libs.jakarta.persistence.api)

    implementation(platform(libs.awssdk.bom))
    implementation(libs.awssdk.sso)
    implementation(libs.awssdk.ssooidc)
    implementation(libs.awssdk.s3)

    implementation(platform(libs.micrometer.bom))
    implementation(libs.micrometer.prometheus)

    implementation(libs.guava)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.redisson)

    runtimeOnly(libs.spring.actuator)
    runtimeOnly(libs.h2)

    testImplementation(libs.spring.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    developmentOnly(libs.spring.devtools)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Jar> {
    archiveBaseName.set("ohmygoods")
    archiveVersion.set("0.0.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
