* Create Kafka topic playground.stickynote.json with 3 partitions and replication factor of 3
```bash
kafka-topics --bootstrap-server kafka1:29092 --create --topic playground.stickynote.json --partitions 3 --replication-factor 3
```

* Create Kafka topic playground.stickynote.json.categorized with 3 partitions and replication factor of 3
```bash
kafka-topics --bootstrap-server kafka1:29092 --create --topic playground.stickynote.json.categorized --partitions 3 --replication-factor 3
```
