# Use a Maven image with a JDK that matches the project compiler target
FROM maven:3.9.6-eclipse-temurin-23 AS build
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml and the project files to the container
COPY pom.xml .
COPY src ./src
# Build the application using Maven
RUN mvn clean package -DskipTests
# Use a JRE image that matches the project compiler target
FROM eclipse-temurin:23-jre
# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the previous stage to the container
ARG JAR_FILE=target/umcs-springframework-1.0-SNAPSHOT.jar
COPY --from=build /app/${JAR_FILE} ./app.jar
# Set the command to run the application
CMD ["java", "-jar", "app.jar"]
