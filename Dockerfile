FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
EXPOSE 8761
ADD ./target/EurekaServer-0.0.1-SNAPSHOT.jar eureka-server.jar
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]