FROM eclipse-temurin:17-jdk
EXPOSE 8100
ADD target/currency-conversion-0.0.1-SNAPSHOT.jar /app/currency-conversion.jar
ENTRYPOINT ["java", "-jar", "/app/currency-conversion.jar"]
