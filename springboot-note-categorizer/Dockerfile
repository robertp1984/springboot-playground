FROM amazoncorretto:25.0.0-alpine

ARG JAR_FILE=./target/springboot-note-categorizer*jar-with-dependencies.jar
ENV KAFKA_BOOTSTRAP_SERVERS=kafka1:29092

RUN addgroup app && adduser -D -G app app

WORKDIR /app
RUN chown app:app /app
USER app

COPY ${JAR_FILE} springboot-note-categorizer.jar

CMD java -jar springboot-note-categorizer.jar ${KAFKA_BOOTSTRAP_SERVERS}
