# Dockerfile（后端）
FROM openjdk:8-jre-alpine

WORKDIR /app
COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]

