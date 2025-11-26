FROM openjdk:8-jdk-alpine

WORKDIR /app

COPY ./user-center-backend-0.0.1-SNAPSHOT.jar ./app/application.jar

EXPOSE 8080

CMD ["java","-jar","./app/UserCenter-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]

