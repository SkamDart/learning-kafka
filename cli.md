# Kafka CLI
##  kafka-topics (kt)
### Create Topic
```bash
$ kt --zookeeper $ZOOKEEPER_HOST \
     --topic $TOPIC_NAME \
     --create --partitions $PARTITION_COUNT \
     --replication_factor $REPLICATION_FACTOR
```

### List Topics
```bash
$ kt --zookeeper $ZOOKEERPER_HOST --list
```

### Delete Topic
```bash
$ kt --zookeeper $ZOOKEEPER_HOST \
	  --topic $TOPIC_NAME \
	  --delete
```

### Describe Topic
```bash $ kt --zookeeper $ZOOKEEPER_HOST \
	  --topic $TOPIC_NAME \
	  --describe
```
**Gotchas**
1. Replication factor cannot be larger than number of brokers
2. Use `.` or `_` but not both

## kafka-console-producer (kcp)
### Specify Acks
```bash
$ kcp --broker-list $BROKER_LIST \
	   --topic $TOPIC_NAME \
	   --producer-property acks=all 
> some acked message
```

## kafka-console-consumer (kcc)
### Consume topic
```bash
kcc --bootstrap-server $KAFKA_BOOTSTRAP \
	--from-beginning \
	--topic $TOPIC_NAME
```
Notes

- bootstrap server can be any server in kafka cluster
