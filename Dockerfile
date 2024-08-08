# build stage
FROM alpine/java:21-jdk AS build
ARG GRADLE_VERSION=8.8
WORKDIR /app
RUN apk add --no-cache curl \
    && curl -L https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o /tmp/gradle.zip \
    && unzip -d /opt /tmp/gradle.zip \
    && rm /tmp/gradle.zip \
    && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle
COPY . /app
RUN chmod +x gradle && gradle build

# final stage
FROM alpine/java:21-jdk


ENV API_USER_TOKEN="Bearer <Token>"
ENV SERVER_PORT=5050
ARG APP_VERSION=1.0.0-SNAPSHOT

WORKDIR /app
COPY --from=build /app/build/libs/DirectusManager-${APP_VERSION}.jar /app/appication.jar
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java", "-jar", "/app/appication.jar"]