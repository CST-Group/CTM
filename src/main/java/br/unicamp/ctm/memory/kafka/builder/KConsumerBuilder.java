package br.unicamp.ctm.memory.kafka.builder;

import br.unicamp.ctm.memory.kafka.exception.TopicNotFoundException;
import br.unicamp.ctm.memory.kafka.TopicConfigProvider;
import br.unicamp.ctm.memory.kafka.config.TopicConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Logger;

public class KConsumerBuilder {

  private static final Logger logger = Logger.getLogger(KConsumerBuilder.class);

  private static KafkaConsumer<String, String> buildConsumer(String brokers, String consumerGroupId,
      String topic) {

    KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(
        buildConsumerProperties(brokers, consumerGroupId));

    kafkaConsumer.subscribe(Collections.singletonList(topic));

    return configureConsumer(kafkaConsumer);
  }


  public static Properties buildConsumerProperties(String brokers, String consumerGroupId) {

    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return properties;
  }


  private static KafkaConsumer<String, String> configureConsumer(
      KafkaConsumer<String, String> consumer) {

    consumer.poll(Duration.ofSeconds(10));

    AtomicLong maxTimestamp = new AtomicLong();
    AtomicReference<ConsumerRecord<String, String>> latestRecord = new AtomicReference<>();

    consumer.endOffsets(consumer.assignment()).forEach((topicPartition, offset) -> {
      consumer.seek(topicPartition, (offset == 0) ? offset : offset - 1);
      consumer.poll(Duration.ofSeconds(10)).forEach(record -> {
        if (record.timestamp() > maxTimestamp.get()) {
          maxTimestamp.set(record.timestamp());
          latestRecord.set(record);
        }
      });
    });

    return consumer;
  }


  public static Map<TopicConfig, KafkaConsumer<String, String>> generateConsumers(
      List<TopicConfig> topicConfigs, String consumerGroupID) {

    Map<TopicConfig, KafkaConsumer<String, String>> consumers = new HashMap<>();

    topicConfigs.forEach(topicConfig -> {
      if (topicConfig.getRegexPattern() != null) {
        logger.info(String.format("Regex pattern %s identified.", topicConfig.getRegexPattern()));

        if (!topicConfig.getRegexPattern().isEmpty()) {
          try {
            final List<TopicConfig> foundTopics =
                TopicConfigProvider.generateTopicConfigsPrefix(topicConfig.getBroker(),
                    topicConfig.getRegexPattern(), topicConfig.getClassName());

            if (foundTopics.size() == 0) {
              throw new TopicNotFoundException(
                  String.format("Topic prefix not found - Prefix - %s.",
                      topicConfig.getRegexPattern()));
            }

            Map<TopicConfig, KafkaConsumer<String, String>> regexPatternConsumers = generateConsumers(
                foundTopics, consumerGroupID);
            consumers.putAll(regexPatternConsumers);
          } catch (TopicNotFoundException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
          }

          return;
        }
      }

      logger.info(String.format(
          "Creating consumer for topic configuration - Name: %s - Broker: %s - Class: %s - Behavior Type: %s",
          topicConfig.getName(),
          topicConfig.getBroker(),
          topicConfig.getClassName(),
          topicConfig.getDistributedMemoryBehavior()));

      final KafkaConsumer<String, String> consumer =
          buildConsumer(topicConfig.getBroker(), consumerGroupID, topicConfig.getName());

      logger.info(String.format("Consumer created for topic %s.", topicConfig.getName()));

      consumers.put(topicConfig, consumer);
    });

    return consumers;
  }


}
