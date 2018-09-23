# Kafka CLI Overview
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
```bash
$ kt --zookeeper $ZOOKEEPER_HOST \
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
	--topic $TOPIC_NAME
```
Notes

- bootstrap server can be any server in kafka cluster
- Using the flag --from-beginning we read all messages for *that partition*
- Order is only guaranteed within a partition

**Question:** How would you get a stream of all messages to a single topic? (Total Ordering)

**Answer:** A single topic with a single partition is the only way to guarantee this behavior (total ordering). 


### Consumer Groups
```bash
$ kcc --boostrap-server $KAFKA_BOOTSTRAP \
	  --topic $TOPIC_NAME \
	  --group $GROUP_NAME
```

Consumer groups with load balance round


## kafka-consumer-group (kcg)
### List Groups
```bash
$ kcg --boostrap-server $KAFKA_BOOTSTRAP --list
```

### Describe Group
```bash
$ kcg --boostrap-server $KAFKA_BOOTSTRAP \
	  --describe \
	  --group $GROUP_NAME
```


### Offsets
#### All topics from beginning
```bash
$ kcg --boostrap-server $KAFKA_BOOSTRAP \
      --group $GROUP_NAME \
      --reset-offsets \
      --to-earliest \
      --execute
      --all-topics
```

#### Reset $TOPIC_NAME from beginning
```bash
$ kcg --boostrap-server $KAFKA_BOOTSTRAP \
      --group $GROUP_NAME \
      --reset-offsets \
      --to-earliest \
      --execute \
      --topic_name $TOPIC_NAME
```

#### Shift Offset
```bash
$ kcg --bootstrap-server $KAFKA_BOOTSTRAP \
      --group $GROUP_NAME \
      --reset-offsets \
      --shift-by 2 \
      --execute \ 
      --topic $TOPIC_NAME
```      
