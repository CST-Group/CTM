package br.unicamp.dct.memory;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.dct.kafka.ConsumerBuilder;
import br.unicamp.dct.kafka.ProducerBuilder;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DistributedMemory implements Memory {

    private String name;
    private String brokers;
    private List<TopicConfig> topics;
    private DistributedMemoryType type;
    private List<Memory> memories;
    private List<MemoryWriterThread> memoryWriterThreads;
    private List<MemoryReaderThread> memoryReaderThreads;

    private Logger logger;

    public DistributedMemory(String name, String brokers, List<TopicConfig> topics) {
        memorySetup(name, brokers, type, topics);
    }

    private void memorySetup(String name, String brokers, DistributedMemoryType type,
                             List<TopicConfig> topics) {
        this.name = name;
        this.topics = topics;
        this.type = type;
        this.brokers = brokers;

        this.memories = new ArrayList<>();
        this.memoryWriterThreads = new ArrayList<>();
        this.memoryReaderThreads = new ArrayList<>();

        this.logger = LoggerFactory.getLogger(DistributedMemory.class);

        if (type == DistributedMemoryType.INPUT_MEMORY) {
            generateConsumers(topics);
        } else
            generateProducers(topics);
    }

    private List<TopicConfig> getTopicsFromKafka(String prefix) {
        final KafkaConsumer<String, String> any = ConsumerBuilder.buildConsumer(brokers, "any");
        final Map<String, List<PartitionInfo>> topicsInfo = any.listTopics();
        final List<String> foundTopics =
                topicsInfo.keySet().stream().filter(partitionInfos -> partitionInfos.contains(prefix))
                        .collect(Collectors.toList());
        any.close();

        return foundTopics.stream().map(topic -> new
                TopicConfig(topic, DistributedMemoryBehavior.PULLED, DistributedMemoryType.INPUT_MEMORY)
        ).collect(Collectors.toList());
    }

    private void generateConsumers(List<TopicConfig> topics) {
        topics.forEach(topic -> {
            final KafkaConsumer<String, String> consumer =
                    ConsumerBuilder.buildConsumer(brokers, name);

            if(topic.getPrefix()!= null) {
                if(!topic.getPrefix().isEmpty()) {
                    final List<TopicConfig> foundTopics = getTopicsFromKafka(topic.getPrefix());
                    generateConsumers(foundTopics);
                }
            }

            consumer.subscribe(Collections.singletonList(topic.getName()));

            final Memory memory = createMemoryObject(String.format("%s_DM", topic));
            getMemories().add(memory);

            MemoryWriterThread memoryWriterThread = new MemoryWriterThread(memory, consumer);
            memoryWriterThread.start();

            getMemoryWriterThreads().add(memoryWriterThread);
        });
    }

    private void generateProducers(List<TopicConfig> topics) {
        topics.forEach(topic -> {
            final KafkaProducer<String, String> producer =
                    ProducerBuilder.buildProducer(brokers);

            final Memory memory = createMemoryObject(String.format("%s_DM", topic.getName()));
            getMemories().add(memory);

            MemoryReaderThread memoryReaderThread = new MemoryReaderThread(memory, producer, topic.getDistributedMemoryBehavior());
            memoryReaderThread.start();

            getMemoryReaderThreads().add(memoryReaderThread);
        });
    }

    private MemoryObject createMemoryObject(String name) {
        MemoryObject memoryObject = new MemoryObject();
        memoryObject.setTimestamp(System.currentTimeMillis());
        memoryObject.setEvaluation(0.0d);
        memoryObject.setType(name);

        return memoryObject;
    }


    @Override
    public Object getI() {
        final Memory memory = memories.stream().max(Comparator.comparing(Memory::getEvaluation)).orElse(null);
        return memory != null ? memory.getI() : null;
    }

    public Object getI(int index) {
        try {
            return memories.get(index).getI();
        } catch (IndexOutOfBoundsException ex) {
            logger.error(String.format("Impossible to get memory content. Index %s out of bounds.", index));
            return null;
        }
    }

    public Object getI(String name) {
        final Optional<Memory> memoryOptional =
                memories.stream().filter(memory -> memory.getName().equals(name)).findFirst();

        return memoryOptional.orElse(null) != null ? memoryOptional.get().getI() : null;
    }


    @Override
    public int setI(Object info) {
        try {
            return memories.get(0).setI(info);
        } catch (IndexOutOfBoundsException ex) {
            logger.error("Impossible to set memory content. Index 0 out of bounds.");
            return -1;
        }
    }

    public int setI(Object info, int index) {
        try {
            return memories.get(index).setI(info);
        } catch (IndexOutOfBoundsException ex) {
            logger.error(String.format("Impossible to set memory content. Index %s out of bounds.", index));
            return -1;
        }
    }

    public int setI(Object info, double evaluation, int index) {
        try {
            memories.get(index).setEvaluation(evaluation);
            return memories.get(index).setI(info);
        } catch (IndexOutOfBoundsException ex) {
            logger.error(String.format("Impossible to set memory content. Index %s out of bounds.", index));
            return -1;
        }
    }

    @Override
    public Double getEvaluation() {
        final Memory memory = memories.stream().max(Comparator.comparing(Memory::getEvaluation)).orElse(null);
        return memory != null ? memory.getEvaluation() : -1;
    }

    @Override
    public void setEvaluation(Double evaluation) {
        try {
            memories.get(0).setEvaluation(evaluation);
        } catch (IndexOutOfBoundsException ex) {
            logger.error("Impossible to set memory evaluation. Index 0 out of bounds.");
        }
    }

    public void setEvaluation(Double evaluation, int index) {
        try {
            memories.get(index).setEvaluation(evaluation);
        } catch (IndexOutOfBoundsException ex) {
            logger.error(String.format("Impossible to set memory evaluation. Index %s out of bounds.", index));
        }
    }

    @Override
    public Long getTimestamp() {
        final Memory memory = memories.stream().max(Comparator.comparing(Memory::getEvaluation)).orElse(null);
        return memory != null ? memory.getTimestamp() : null;
    }

    public Long getTimestamp(int index) {
        try {
            return memories.get(index).getTimestamp();
        } catch (IndexOutOfBoundsException ex) {
            logger.error(String.format("Impossible to get memory timestamp. Index %s out of bounds.", index));
            return null;
        }
    }

    public void addTopic(TopicConfig topic) {
        if (type == DistributedMemoryType.INPUT_MEMORY) {
            generateConsumers(Collections.singletonList(topic));
        } else {
            generateProducers(Collections.singletonList(topic));
        }
    }


    @Override
    public String getName() {
        return name;
    }

    public List<Memory> getMemories() {
        return memories;
    }

    public List<TopicConfig> getTopics() {
        return topics;
    }

    public List<MemoryWriterThread> getMemoryWriterThreads() {
        return memoryWriterThreads;
    }

    public List<MemoryReaderThread> getMemoryReaderThreads() {
        return memoryReaderThreads;
    }
}
