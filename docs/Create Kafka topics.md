* Create Kafka topic playground.stickynote with 3 partitions and replication factor of 3
```bash
kafka-topics --bootstrap-server kafka1:29092 --create --topic playground.stickynote --partitions 3 --replication-factor 3
```

* Create Kafka topic playground.stickynote.categorized with 3 partitions and replication factor of 3
```bash
kafka-topics --bootstrap-server kafka1:29092 --create --topic playground.stickynote.categorized --partitions 3 --replication-factor 3
```
