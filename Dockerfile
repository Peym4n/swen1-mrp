# 1. Use a lightweight Java 23 runtime (Alpine Linux)
FROM eclipse-temurin:23-jre-alpine

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy the compiled JAR file from your target folder to the container
# Note: The CI pipeline runs 'mvn package' first, so this file will exist.
COPY target/mrp*.jar app.jar

# 4. Expose the port your HttpServer uses
EXPOSE 8080

# 5. Define the command to run your app
ENTRYPOINT ["java", "-jar", "app.jar"]