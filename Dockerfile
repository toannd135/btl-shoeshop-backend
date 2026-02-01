# build stage
FROM maven:3.9-eclipse-temurin-21-alpine as build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . . 
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /run
COPY --from=build /app/target/*.jar /run/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/run/app.jar"]
