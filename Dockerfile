# 🏗️ Build stage
FROM eclipse-temurin:17-jdk as build

WORKDIR /app

# Copy and fix mvnw permissions and line endings
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY . .

# Package the Spring Boot application (skip tests)
RUN ./mvnw clean package -DskipTests

# 🚀 Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
