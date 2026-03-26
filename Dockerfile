# Stage 1: Build with Maven
FROM maven:3.8-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run with lightweight JRE
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY --from=build /app/target/java-case-study-1.0.0.jar app.jar

# default: 10 threads, 50 certificates
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["10", "50"]
