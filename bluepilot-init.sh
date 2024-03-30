#!/bin/bash

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    # Docker not found, install Docker
    echo "Docker not found. Installing Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    rm get-docker.sh
    echo "Docker installed successfully!"
else
    echo "Docker is already installed."
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    # Docker Compose not found, install Docker Compose
    echo "Docker Compose not found. Installing Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo "Docker Compose installed successfully!"
else
    echo "Docker Compose is already installed."
fi

# Check if Gradle is installed
if ! command -v gradle &> /dev/null; then
    # Gradle not found, install Gradle
    echo "Gradle not found. Installing Gradle..."
    SDKMAN_DIR="$HOME/.sdkman"
    curl -s "https://get.sdkman.io" | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
    sdk install gradle 7.2
    echo "Gradle installed successfully!"
else
    echo "Gradle is already installed."
fi

# Check if Java 17 is installed
if ! command -v java &> /dev/null || [[ "$(java -version 2>&1)" != *"17."* ]]; then
    # Java 17 not found, install Java 17
    echo "Java 17 not found. Installing Java 17..."
    sudo apt update
    sudo apt install -y openjdk-17-jdk
    echo "Java 17 installed successfully!"
else
    echo "Java 17 is already installed."
fi

# Check if SonarQube is installed using Docker
if ! docker ps -a | grep sonarqube &> /dev/null; then
    # SonarQube not found, run SonarQube using Docker
    echo "SonarQube not found. Running SonarQube with Docker..."
    docker run -d --name sonarqube -p 9000:9000 -p 9092:9092 sonarqube
    echo "SonarQube started successfully!"
else
    echo "SonarQube is already running."
fi

# Run Docker Compose file
echo "Running Docker Compose..."
docker-compose up
