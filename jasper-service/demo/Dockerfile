# Lightweight Java Image
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

# Copy the built jar into the container
COPY target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Headless mode is still required
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]
