# Create the connector in Kafka Connect which will read from the "playground.stickynote.json" topic and write to MongoDB

curl -X DELETE http://localhost:8083/connectors/mongodb-stickynote-sink1
curl -X POST -H "Content-Type: application/json" -T stickynote-sink-to-mongodb-request.json http://localhost:8083/connectors

