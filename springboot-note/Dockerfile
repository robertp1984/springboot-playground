FROM amazoncorretto:25.0.0-alpine

RUN addgroup app && adduser -D -G app app

WORKDIR /app
RUN chown app:app /app
USER app


COPY target/springboot-note*.jar springboot-note.jar

EXPOSE 8080

CMD ["java", "-jar", "springboot-note.jar"]
