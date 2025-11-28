# Dockerfile（后端）
FROM openjdk:8-jdk-alpine AS build
RUN apk add --no-cache maven
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]

