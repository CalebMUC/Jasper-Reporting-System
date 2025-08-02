# ------------ Stage 1: Build the JAR using Maven ------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Build the project and skip tests
RUN mvn clean package -DskipTests


# ------------ Stage 2: Create the final image ------------
FROM openjdk:17-jdk-slim

# Install CA certs, fonts, freetype, and rendering dependencies
RUN apt-get update && apt-get install -y \
    ca-certificates \
    fonts-dejavu-core \
    fonts-dejavu-extra \
    fonts-liberation \
    libfreetype6 \
    libfontconfig1 \
    libxrender1 \
    libxext6 \
    libx11-6 \
 && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run in headless mode
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]
