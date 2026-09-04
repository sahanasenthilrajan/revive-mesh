# Build stage
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY ca.pem .
# Try to download dependencies, but don't fail if we don't have source code yet
RUN mvn dependency:go-offline -B || true
COPY src ./src
RUN mvn clean package -DskipTests

# Build the Kafka TLS truststore deterministically from the Aiven Project CA.
# No secrets involved; changeit is the conventional JKS default (not a Kafka credential).
RUN keytool -importcert -noprompt \
    -alias aiven-ca \
    -file ca.pem \
    -keystore kafka-truststore.jks \
    -storepass changeit \
    -storetype JKS

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/backend-*.jar app.jar
COPY --from=build /app/kafka-truststore.jks kafka-truststore.jks
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "case \"${DATABASE_URL:-}\" in jdbc:postgresql://*) export JDBC_DATABASE_URL=\"${DATABASE_URL}\";; postgresql://*|postgres://*) database_host_path=\"${DATABASE_URL#*://}\"; database_host_path=\"${database_host_path#*@}\"; export JDBC_DATABASE_URL=\"jdbc:postgresql://${database_host_path}\";; esac; exec java -jar app.jar"]
