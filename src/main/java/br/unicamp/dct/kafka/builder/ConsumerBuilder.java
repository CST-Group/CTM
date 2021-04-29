package br.unicamp.dct.kafka.builder;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.dct.exception.TopicNotFoundException;
import br.unicamp.dct.kafka.TopicConfigProvider;
import br.unicamp.dct.kafka.config.TopicConfig;
import br.unicamp.dct.memory.DistributedMemory;
import br.unicamp.dct.thread.MemoryContentReceiverThread;
import kafka.Kafka;
import kafka.security.auth.Topic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ConsumerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerBuilder.class);

    private static KafkaConsumer<String, String> buildConsumer(String brokers, String consumerGroupId, String topic){

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


    public static Map<TopicConfig, KafkaConsumer<String, String>> generateConsumers(List<TopicConfig> topics, String brokers, String consumerGroupID) {

        Map<TopicConfig, KafkaConsumer<String, String>> consumers = new HashMap<>();

        topics.forEach(topic -> {
            if (topic.getPrefix() != null) {
                if (!topic.getPrefix().isEmpty()) {
                    try {
                        final List<TopicConfig> foundTopics =
                                TopicConfigProvider.generateTopicConfigsPrefix(brokers, topic.getPrefix(), topic.getClassName());

                        Map<TopicConfig, KafkaConsumer<String, String>> prefixConsumers = generateConsumers(foundTopics, brokers, consumerGroupID);
                        consumers.putAll(prefixConsumers);
                    } catch (TopicNotFoundException e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    }

                    return;
                }
            }

            final KafkaConsumer<String, String> consumer =
                    buildConsumer(brokers, consumerGroupID, topic.getName());

            consumers.put(topic, consumer);
        });

        return consumers;
    }


}
