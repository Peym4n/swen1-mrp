# 1. Use a lightweight Java 23 runtime (Alpine Linux)
FROM eclipse-temurin:23-jre-alpine

# 2. Create a group and user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 3. Set the working directory inside the container
WORKDIR /app

# 3. Copy the compiled JAR file from target folder to the container
# Note: The CI pipeline runs 'mvn package' first, so this file will exist.
COPY target/mrp*.jar app.jar

# 4. Switch to the non-root user
USER appuser

# 5. Expose the port HttpServer uses
EXPOSE 8080

# 6. Define the command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]