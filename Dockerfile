FROM openjdk:17

WORKDIR /app

ADD target/codecinema-1.0-SNAPSHOT.jar codecinema.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "codecinema.jar"]
