#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean install

#
# Package stage
#
FROM eclipse-temurin:17-jdk
COPY --from=build /target/book-store-be-0.0.1-SNAPSHOT.jar book-store-be.jar

# Expose the necessary port
EXPOSE 8081

# Start the application
ENTRYPOINT ["java", "-jar", "book-store-be.jar"]
