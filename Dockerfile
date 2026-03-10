# build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . . 
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests
# run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /run
RUN apk add --no-cache wget
COPY --from=build /app/target/*.jar /run/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/run/app.jar"]
