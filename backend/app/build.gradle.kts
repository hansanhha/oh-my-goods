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
    implementation(libs.spring.data.redis)
    implementation(libs.spring.security)
    implementation(libs.spring.oauth2.client)

    implementation(platform(libs.awssdk.bom))
    implementation(libs.awssdk.sso)
    implementation(libs.awssdk.ssooidc)
    implementation(libs.awssdk.s3)

    implementation(libs.guava)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

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
