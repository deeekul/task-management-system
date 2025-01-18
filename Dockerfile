FROM openjdk:21-jdk-slim

WORKDIR /app

COPY /build/libs/taskManagementSystem-0.1.0.jar ./taskManagementSystem.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "taskManagementSystem.jar"]