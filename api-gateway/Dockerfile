FROM eclipse-temurin:17-jdk
EXPOSE 8765
ADD target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar
ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]
