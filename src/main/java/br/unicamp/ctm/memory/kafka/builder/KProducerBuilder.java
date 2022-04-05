package br.unicamp.ctm.memory.kafka.builder;

import br.unicamp.ctm.memory.kafka.config.TopicConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

public class KProducerBuilder {

  private static final Logger logger = Logger.getLogger(KProducerBuilder.class);

  public static KafkaProducer<String, String> buildProducer(String brokers) {
    return new KafkaProducer<>(buildProducerProperties(brokers));
  }

  public static Properties buildProducerProperties(String brokers) {

    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());

    return properties;
  }

  public static List<KafkaProducer<String, String>> generateProducers(List<TopicConfig> topics) {

    List<KafkaProducer<String, String>> producers = new ArrayList<>();

    topics.forEach(topicConfig -> {

      logger.info(String.format(
          "Creating producer for topic configuration - Name: %s - Broker: %s - Class: %s - Behavior Type: %s",
          topicConfig.getName(),
          topicConfig.getBroker(),
          topicConfig.getClassName(),
          topicConfig.getDistributedMemoryBehavior()));

      final KafkaProducer<String, String> producer =
          KProducerBuilder.buildProducer(topicConfig.getBroker());

      logger.info(String.format("Producer created for topic %s.", topicConfig.getName()));

      producers.add(producer);
    });

    return producers;
  }


}
