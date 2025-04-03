FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 8888
ADD ./target/ConfigServer-0.0.1-SNAPSHOT.jar config-server.jar

ENTRYPOINT ["java", "-jar", "config-server.jar"]