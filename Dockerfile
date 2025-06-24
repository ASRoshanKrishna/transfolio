# 🏗️ Build stage
FROM eclipse-temurin:17-jdk as build

WORKDIR /app

# Copy Maven wrapper files and give execute permissions
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy rest of the app
COPY . .

# Package the Spring Boot app (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# 🚀 Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
