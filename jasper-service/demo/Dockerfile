#lightweight Java Image
From openjdk:17-jdk-slim
# Working Directory inside the container
WORKDIR /app
#copy the jar built File in the Container
COPY target/*.jar app.jar
#Expose the port
EXPOSE 8080
#Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

