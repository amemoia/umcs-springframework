FROM maven:latest AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
FROM eclipse-temurin:latest
WORKDIR /app
ARG JAR_FILE=target/umcs-springframework-1.0-SNAPSHOT.jar
COPY --from=build /app/${JAR_FILE} ./app.jar
CMD ["java", "-jar", "app.jar"]
