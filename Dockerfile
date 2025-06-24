# Start from an OpenJDK image
FROM eclipse-temurin:17-jdk as build

# Set workdir inside the container
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy the rest of your project
COPY . .

# Package the Spring Boot app
RUN ./mvnw clean package -DskipTests

# Run phase
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy only the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
