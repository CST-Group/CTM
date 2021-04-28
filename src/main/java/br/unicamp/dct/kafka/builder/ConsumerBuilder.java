package br.unicamp.dct.kafka.builder;

import br.unicamp.dct.kafka.TopicConfigProvider;
import br.unicamp.dct.kafka.config.TopicConfig;
import kafka.Kafka;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ConsumerBuilder {

    public static KafkaConsumer<String, String> buildConsumer(String brokers, String consumerGroupId, String topic){

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(buildConsumerProperties(brokers, consumerGroupId));

        kafkaConsumer.subscribe(Collections.singletonList(topic));

        return configureConsumer(kafkaConsumer);
    }


    public static Properties buildConsumerProperties(String brokers, String consumerGroupId){

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return properties;
    }


    private static KafkaConsumer<String, String> configureConsumer(KafkaConsumer<String, String> consumer) {

        consumer.poll(Duration.ofSeconds(10));

        AtomicLong maxTimestamp = new AtomicLong();
        AtomicReference<ConsumerRecord<String, String>> latestRecord = new AtomicReference<>();

        consumer.endOffsets(consumer.assignment()).forEach((topicPartition, offset) -> {
            consumer.seek(topicPartition, (offset==0) ? offset:offset - 1);
            consumer.poll(Duration.ofSeconds(10)).forEach(record -> {
                if (record.timestamp() > maxTimestamp.get()) {
                    maxTimestamp.set(record.timestamp());
                    latestRecord.set(record);
                }
            });
        });

        return consumer;
    }
}
