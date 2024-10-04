plugins {
    application
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot)
    implementation(libs.spring.web)
    implementation(libs.spring.validation)
    implementation(libs.spring.data.jpa)
    implementation(libs.spring.security)
    implementation(libs.spring.oauth2.client)

    implementation(libs.guava)

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

tasks.named<Test>("test") {
    useJUnitPlatform()
}
