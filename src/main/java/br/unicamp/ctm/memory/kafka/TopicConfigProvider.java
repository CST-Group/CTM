package br.unicamp.ctm.memory.kafka;

import br.unicamp.ctm.memory.kafka.exception.TopicNotFoundException;
import br.unicamp.ctm.memory.kafka.builder.KConsumerBuilder;
import br.unicamp.ctm.memory.kafka.config.TopicConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TopicConfigProvider {

    public static List<TopicConfig> generateTopicConfigsPrefix(String brokers, String prefix, String className) throws TopicNotFoundException {
        final KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(
            KConsumerBuilder.buildConsumerProperties(brokers, "any"));
        final Map<String, List<PartitionInfo>> topicsInfo = kafkaConsumer.listTopics();

        final List<String> topics =
                new ArrayList<>(topicsInfo.keySet());

        Pattern pattern = Pattern.compile(prefix);
        List<String> foundTopics = topics.stream().filter(pattern.asPredicate()).collect(Collectors.toList());

        kafkaConsumer.close();

        if (foundTopics.size() == 0) {
            throw new TopicNotFoundException("Topics not found. Review regex pattern.");
        }

        return foundTopics.stream().map(topic -> new
                TopicConfig(topic, KDistributedMemoryBehavior.PULLED, className)
        ).collect(Collectors.toList());
    }

}
