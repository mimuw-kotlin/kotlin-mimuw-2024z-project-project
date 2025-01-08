#!/bin/bash

# Stop and remove containers, networks, images, and volumes
docker-compose down

# Build the fat JAR using Gradle
./gradlew buildFatJar

# Build the Docker images
docker-compose build

# Run the Docker container with specified port mappings so
# that they are exposed and the application is accessible from the host machine
docker-compose run -p 8080:8080 -p 8443:8443 web