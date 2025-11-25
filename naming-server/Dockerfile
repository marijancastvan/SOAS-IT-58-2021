FROM eclipse-temurin:17-jdk
EXPOSE 8761
ADD target/naming-server-0.0.1-SNAPSHOT.jar /app/naming-server.jar
ENTRYPOINT ["java", "-jar", "/app/naming-server.jar"]
