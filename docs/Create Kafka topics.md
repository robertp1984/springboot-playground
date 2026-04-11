* Create Kafka topic playground.stickynote.json with 9 partitions and replication factor of 3
```bash
kafka-topics --bootstrap-server kafka1:29092 --create --topic playground.stickynote.json --partitions 9 --replication-factor 3
```

* Create Kafka topic playground.stickynote.json.categorized with 9 partitions and replication factor of 3
```bash
kafka-topics --bootstrap-server kafka1:29092 --create --topic playground.stickynote.json.categorized --partitions 9 --replication-factor 3
```
