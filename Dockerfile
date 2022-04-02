# Build stage
FROM maven:3.8-openjdk-8 as build

WORKDIR /build
COPY . .

RUN mvn --batch-mode clean validate
RUN mvn --batch-mode package -Dmaven.test.skip=true

COPY ./ApiServer-1.0-jar-with-dependencies.jar ./ApiServer.jar

FROM openjdk:8-alpine as run

ENV port 8080
ENV host localhost
ENV baseApiPath api
ENV baseUrl http://localhost:8080/api
ENV galleryPath /gallery
ENV databaseFilePath /data/data.db
ENV usageLogFilePath /data/usage.jsonl

VOLUME /gallery
VOLUME /data

WORKDIR /app

COPY --from=build /build/ApiServer.jar ./ApiServer.jar

CMD ["java", "-jar", "ApiServer.jar"]
