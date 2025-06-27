FROM openjdk:21-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y curl
COPY target/page-service-0.0.1-SNAPSHOT.jar /app/page-service.jar
ENTRYPOINT ["java", "-jar", "page-service.jar"]