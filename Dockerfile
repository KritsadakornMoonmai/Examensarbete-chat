# Use an official Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory
WORKDIR /app

# Copy pom.xml and install dependencies
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Use a lightweight JRE image for running the app
FROM eclipse-temurin:17-jre

# Set working directory in the runtime container
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

COPY .env .env

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]