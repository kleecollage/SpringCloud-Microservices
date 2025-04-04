FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

ADD ./target/Users-0.0.1-SNAPSHOT.jar msvc-users.jar

ENTRYPOINT ["java", "-jar", "msvc-users.jar"]