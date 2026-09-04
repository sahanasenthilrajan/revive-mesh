# Build stage
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Try to download dependencies, but don't fail if we don't have source code yet
RUN mvn dependency:go-offline -B || true
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "case \"${DATABASE_URL:-}\" in jdbc:postgresql://*) export JDBC_DATABASE_URL=\"${DATABASE_URL}\";; postgresql://*|postgres://*) database_host_path=\"${DATABASE_URL#*://}\"; database_host_path=\"${database_host_path#*@}\"; export JDBC_DATABASE_URL=\"jdbc:postgresql://${database_host_path}\";; esac; exec java -jar app.jar"]
