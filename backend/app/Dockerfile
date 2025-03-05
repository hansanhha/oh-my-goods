FROM gradle:8.12-alpine AS builder
WORKDIR /builder

COPY ./app .
COPY ./gradle/libs.versions.toml ./gradle/libs.versions.toml
COPY ./settings.gradle.kts .
RUN gradle clean build

FROM openjdk:21-slim
WORKDIR /ohmygoods
COPY --from=builder /builder/build/libs/ohmygoods*.jar ./app.jar
ENTRYPOINT [ "java", "-jar", "/ohmygoods/app.jar" ]