FROM openjdk:21-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y curl
COPY target/product-service-0.0.1-SNAPSHOT.jar /app/product-service.jar
ENTRYPOINT ["java", "-jar", "product-service.jar"]