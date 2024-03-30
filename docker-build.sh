#!/bin/bash

# Log in to Docker Hub
echo "$DOCKER_HUB_PASSWORD" | docker login -u "$DOCKER_HUB_USERNAME" --password-stdin

# Build and push Docker image
cd eureka-server-registry
docker build -t $DOCKER_HUB_USERNAME/eureka-server-registry-$ENV:latest .
docker push "$DOCKER_HUB_USERNAME/eureka-server-registry-$ENV:latest"
cd ..

cd blue-pilot-gateway
docker build -t $DOCKER_HUB_USERNAME/blue-pilot-gateway-$ENV:latest .
docker push "$DOCKER_HUB_USERNAME/blue-pilot-gateway-$ENV:latest"
cd ..

cd core-service
docker build -t $DOCKER_HUB_USERNAME/core-service-$ENV:latest .
docker push "$DOCKER_HUB_USERNAME/core-service-$ENV:latest"
cd ..

cd auth-service
docker build -t $DOCKER_HUB_USERNAME/auth-service-$ENV:latest .
docker push "$DOCKER_HUB_USERNAME/auth-service-$ENV:latest"
cd ..

cd user-service
docker build -t $DOCKER_HUB_USERNAME/user-service-$ENV:latest .
docker push "$DOCKER_HUB_USERNAME/user-service-$ENV:latest"
cd ..

cd notification-service
docker build -t $DOCKER_HUB_USERNAME/notification-service-$ENV:latest .
docker push "$DOCKER_HUB_USERNAME/notification-service-$ENV:latest"
cd ..

# Log out from Docker Hub (optional)
docker logout
