FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

COPY model/pom.xml model/
COPY domain/pom.xml domain/
COPY adapter/pom.xml adapter/

RUN ./mvnw dependency:go-offline -B

COPY model/src model/src
COPY domain/src domain/src
COPY adapter/src adapter/src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/adapter/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]