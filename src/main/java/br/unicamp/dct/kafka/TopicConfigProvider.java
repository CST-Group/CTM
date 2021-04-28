package br.unicamp.dct.kafka;

import br.unicamp.dct.kafka.builder.ConsumerBuilder;
import br.unicamp.dct.kafka.config.TopicConfig;
import br.unicamp.dct.memory.DistributedMemoryBehavior;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TopicConfigProvider {

    public static List<TopicConfig> generateTopicConfigsPrefix(String brokers, String prefix, String className) {
        final KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(ConsumerBuilder.buildConsumerProperties(brokers, "any"));
        final Map<String, List<PartitionInfo>> topicsInfo = kafkaConsumer.listTopics();

        final List<String> topics =
                new ArrayList<>(topicsInfo.keySet());

        Pattern pattern = Pattern.compile(prefix);
        List<String> foundTopics = topics.stream().filter(pattern.asPredicate()).collect(Collectors.toList());

        kafkaConsumer.close();

        return foundTopics.stream().map(topic -> new
                TopicConfig(topic, DistributedMemoryBehavior.PULLED, className)
        ).collect(Collectors.toList());
    }

}
