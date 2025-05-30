FROM gradle:21-jdk-jammy AS build
LABEL authors="schlingel"
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM eclipse-temurin:21-jdk-jammy
COPY --from=build /home/gradle/src/build/libs/calendar-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]