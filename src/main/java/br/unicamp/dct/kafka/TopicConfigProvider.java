package br.unicamp.dct.kafka;

import br.unicamp.dct.kafka.builder.ConsumerBuilder;
import br.unicamp.dct.kafka.config.TopicConfig;
import br.unicamp.dct.memory.DistributedMemoryBehavior;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TopicConfigProvider {

    public static List<TopicConfig> generateTopicConfigsPrefix(String brokers, String prefix) {
        final KafkaConsumer<String, String> any = ConsumerBuilder.buildConsumer(brokers, "any");
        final Map<String, List<PartitionInfo>> topicsInfo = any.listTopics();

        final List<String> foundTopics =
                topicsInfo.keySet().stream().filter(partitionInfos -> partitionInfos.contains(prefix))
                        .collect(Collectors.toList());
        any.close();

        return foundTopics.stream().map(topic -> new
                TopicConfig(topic, DistributedMemoryBehavior.PULLED)
        ).collect(Collectors.toList());
    }

}
