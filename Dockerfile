FROM openjdk:21-slim

WORKDIR /usr/src/app

COPY target/BachelorWebsocketApplication.jar /usr/src/app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "BachelorWebsocketApplication.jar"]
