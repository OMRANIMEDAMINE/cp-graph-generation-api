# Use the official OpenJDK 17 Alpine image as a parent image
FROM openjdk:17-alpine

# Set the working directory inside the container
WORKDIR /app

# Install Maven and other dependencies
RUN apk --no-cache add maven \
    && apk --no-cache add curl bash tar

# Copy the Maven POM file to the container
COPY pom.xml .

# Build all the dependencies in preparation to go offline.
# This is a separate step so the dependencies will be cached unless the pom file changes.
RUN mvn dependency:go-offline -B

# Copy the project source
COPY src src

# Package the application
RUN mvn package -DskipTests

# Add the required JAR files from the target directory
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Set the java.library.path option
ENV JAVA_OPTS="-Djava.library.path=/opt/ibm/ILOG/CPLEX_Studio_Community2211/opl/bin/x86-64_linux"

# Specify the command to run on container startup
ENTRYPOINT ["java", "${JAVA_OPTS}", "-jar", "app.jar"]
