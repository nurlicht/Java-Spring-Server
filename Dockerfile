# Stage 1: Build Stage
FROM eclipse-temurin:21.0.1_12-jdk-alpine AS build
WORKDIR /app

# Install Maven
RUN apk update && \
    apk add --no-cache maven

# Copy the Maven POM file to the container
COPY pom.xml .

# Download the dependencies and build the JAR file
RUN mvn dependency:go-offline
COPY src src
RUN mvn package -DskipTests

# Stage 2: Runtime Stage
FROM eclipse-temurin:21.0.1_12-jre-alpine
WORKDIR /app

# Install Curl
RUN apk update && \
    apk add --no-cache curl

# Copy only the JAR file from the build stage
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar .

# Copy script for later end-to-end tests
COPY ./e2e/app-test.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/app-test.sh

# Specify the command to run on container startup
CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
