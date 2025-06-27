FROM openjdk:21-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y curl
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar
ENTRYPOINT ["java", "-jar", "user-service.jar"]