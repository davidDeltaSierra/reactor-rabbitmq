FROM maven:3-eclipse-temurin-21-alpine AS build
VOLUME /root/.m2
WORKDIR /app
COPY . /app
RUN mvn package

FROM azul/zulu-openjdk-alpine:21
WORKDIR /app
COPY --from=build /app/target/reactor-rabbitmq.jar .
ENTRYPOINT java -jar reactor-rabbitmq.jar