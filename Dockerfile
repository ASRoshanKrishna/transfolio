FROM eclipse-temurin:17-jdk as build

WORKDIR /app

# Copy Maven wrapper and config first
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn

# Make mvnw executable
RUN chmod +x mvnw

# Pre-download dependencies
RUN ./mvnw dependency:go-offline

# Copy rest of the project and build
COPY . .
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
