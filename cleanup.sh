#!/bin/sh

if sudo docker images | grep -q $DOCKER_HUB_USERNAME/eureka-server-registry-$ENV; then
      # Stop and remove any existing container if it exists
      if sudo docker ps -a | grep -q eureka-server-registry; then
          sudo docker stop eureka-server-registry || true
          sudo docker rm eureka-server-registry || true

      # Remove the Docker image
      sudo docker rmi $DOCKER_HUB_USERNAME/eureka-server-registry-$ENV:latest || true
     fi
fi

if sudo docker images | grep -q $DOCKER_HUB_USERNAME/blue-pilot-gateway-$ENV; then
      # Stop and remove any existing container if it exists
      if sudo docker ps -a | grep -q blue-pilot-gateway; then
          sudo docker stop blue-pilot-gateway || true
          sudo docker rm blue-pilot-gateway || true

      # Remove the Docker image
      sudo docker rmi $DOCKER_HUB_USERNAME/blue-pilot-gateway-$ENV:latest || true
     fi
fi

if sudo docker images | grep -q $DOCKER_HUB_USERNAME/core-service-$ENV; then
      # Stop and remove any existing container if it exists
      if sudo docker ps -a | grep -q core-service; then
          sudo docker stop core-service || true
          sudo docker rm core-service || true

      # Remove the Docker image
      sudo docker rmi $DOCKER_HUB_USERNAME/core-service-$ENV:latest || true
     fi
fi

if sudo docker images | grep -q $DOCKER_HUB_USERNAME/auth-service-$ENV; then
      # Stop and remove any existing container if it exists
      if sudo docker ps -a | grep -q auth-service; then
          sudo docker stop auth-service || true
          sudo docker rm auth-service || true

      # Remove the Docker image
      sudo docker rmi $DOCKER_HUB_USERNAME/auth-service-$ENV:latest || true
     fi
fi

if sudo docker images | grep -q $DOCKER_HUB_USERNAME/user-service-$ENV; then
      # Stop and remove any existing container if it exists
      if sudo docker ps -a | grep -q user-service; then
          sudo docker stop user-service || true
          sudo docker rm user-service || true

      # Remove the Docker image
      sudo docker rmi $DOCKER_HUB_USERNAME/user-service-$ENV:latest || true
     fi
fi

if sudo docker images | grep -q $DOCKER_HUB_USERNAME/notification-service-$ENV; then
      # Stop and remove any existing container if it exists
      if sudo docker ps -a | grep -q notification-service; then
          sudo docker stop notification-service || true
          sudo docker rm notification-service || true

      # Remove the Docker image
      sudo docker rmi $DOCKER_HUB_USERNAME/notification-service-$ENV:latest || true
     fi
fi
