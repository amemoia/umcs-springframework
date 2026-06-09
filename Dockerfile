FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:23-jre
WORKDIR /app
ARG JAR_FILE=target/umcs-springframework-1.0-SNAPSHOT.jar
COPY --from=build /app/${JAR_FILE} ./app.jar
COPY *.json ./
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
