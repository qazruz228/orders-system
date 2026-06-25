FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml ./

RUN mvn -B -ntp dependency:go-offline

COPY src src

RUN mvn -B -ntp package -DskipTests

FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S spring -g 10001 && adduser -S spring -u 10001 -G spring

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/app.jar"]
