# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the Kotlin application files (including build artifacts) into the container
COPY build/libs/auth-service-0.0.1-SNAPSHOT.jar /app

# Expose port 8080 for the Kotlin application (adjust the port number as needed)
EXPOSE 9002

# Define the startup command to run the Kotlin application
CMD ["java", "-jar", "auth-service-0.0.1-SNAPSHOT.jar"]