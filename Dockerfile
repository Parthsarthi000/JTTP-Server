FROM openjdk:17

WORKDIR /app

COPY target/HttpServer-1.0-SNAPSHOT.jar app.jar

COPY src/static /app/static

RUN mkdir -p /app/files

EXPOSE 4221

CMD ["java", "-jar", "app.jar"]