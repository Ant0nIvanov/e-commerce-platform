FROM openjdk:21-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y curl
COPY target/cart-service-0.0.1-SNAPSHOT.jar /app/cart-service.jar
ENTRYPOINT ["java", "-jar", "cart-service.jar"]