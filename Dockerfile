# Build stage
FROM maven:3.8-openjdk-8 as build

WORKDIR /app
COPY . .

RUN mvn --batch-mode clean validate
RUN mvn --batch-mode package

FROM openjdk:8-alpine as run

ENV port 8080
ENV host localhost
ENV baseUrl http://localhost:8080
ENV galleryPath /gallery
ENV databaseFilePath /data/data.db
ENV usageLogFilePath /data/usage.jsonl

VOLUME /gallery
VOLUME /data

WORKDIR /app

COPY --from=build /app/target/ApiServer-1.0-jar-with-dependencies.jar ./ApiServer.jar

CMD ["java", "-jar", "ApiServer.jar"]
