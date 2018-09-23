package com.github.skamdart.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ThreadedConsumerDemo {

    public static void main(String[] args) {
        new ThreadedConsumerDemo().run();
    }

    private void run() {
        Logger logger = LoggerFactory.getLogger(ThreadedConsumerDemo.class.getName());
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my_kafka_app";
        String topic = "first_topic";

        // latch for multithreaded application
        CountDownLatch latch = new CountDownLatch(1);

        logger.info("Creating consumer thread");
        Runnable myConsumerRunnable = new ConsumerRunnable(bootstrapServers, groupId, topic, latch);

        // start thread
        Thread consumerThread = new Thread(myConsumerRunnable);
        consumerThread.start();

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            logger.info("Caught shutdown hook");
            ((ConsumerRunnable) myConsumerRunnable).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("application has exited");
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application Interrupted", e);
        } finally {
            logger.info("Application is closing");
        }
    }

    private ThreadedConsumerDemo() {

    }

    public class ConsumerRunnable implements Runnable {
        private Logger logger;
        private CountDownLatch latch;
        private KafkaConsumer<String, String>  consumer;

        public ConsumerRunnable(String bootstrapServers,
                              String groupId,
                              String topic,
                              CountDownLatch latch) {

            Properties props = new Properties();
            props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            this.latch = latch;
            this.consumer = new KafkaConsumer<>(props);
            this.consumer.subscribe(Collections.singleton(topic));
        }

        @Override
        public void run() {
            try {
               while (true) {
                   ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        logger.info("Key: " + record.key() + "\n" +
                                "Value: " + record.value() + "\n" +
                                "Partition: " + record.partition() + "\n"
                        );
                    }
                }
            } catch (WakeupException e) {
                logger.info("Received shutdown signal");
            } finally {
                consumer.close();
                latch.countDown();
            }

        }

        public void shutdown() {
            consumer.wakeup();
        }
    }
}
