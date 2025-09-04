# STAGE 1: BUILD
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2: TEST
FROM maven:3.9.9-eclipse-temurin-21 AS test
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn dependency:go-offline -B

# STAGE 3: RUN
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]