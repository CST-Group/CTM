package br.unicamp.dct.kafka.builder;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.dct.kafka.config.TopicConfig;
import br.unicamp.dct.thread.MemoryContentReceiverThread;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ProducerBuilder {

    public static KafkaProducer<String, String> buildProducer(String brokers) {
        return new KafkaProducer<>(buildProducerProperties(brokers));
    }

    public static Properties buildProducerProperties(String brokers) {

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }

    public static List<KafkaProducer<String, String>> generateProducers(List<TopicConfig> topics, String brokers) {

        List<KafkaProducer<String, String>> producers = new ArrayList<>();

        topics.forEach(topicConfig -> {
            final KafkaProducer<String, String> producer =
                    ProducerBuilder.buildProducer(brokers);

            producers.add(producer);
        });

        return producers;
    }






}
