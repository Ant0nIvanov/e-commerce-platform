FROM openjdk:21-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y curl
COPY target/auth-service-0.0.1-SNAPSHOT.jar /app/auth-service.jar
ENTRYPOINT ["java", "-jar", "auth-service.jar"]