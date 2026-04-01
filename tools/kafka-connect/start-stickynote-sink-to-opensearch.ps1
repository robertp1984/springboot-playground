# Create the connector in Kafka Connect which will read from the "playground.stickynote.json" topic and write to OpenSearch

curl.exe -X POST -H "Content-Type: application/json" -T stickynote-sink-to-opensearch-request.json http://localhost:8083/connectors

