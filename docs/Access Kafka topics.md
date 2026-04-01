* Read data from Kafka topic playground.stickynote.avro using Avro and Schema Registry
```bash
kafka-avro-console-consumer --bootstrap-server kafka1:29092 --topic playground.stickynote.avro --from-beginning --property schema.registry.url=http://schema-registry:8081 
```
