FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar
COPY src/main/resources/test-scenarios/* test-scenarios/

CMD ["java", "-jar", "app.jar"]