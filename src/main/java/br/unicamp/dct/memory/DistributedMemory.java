package br.unicamp.dct.memory;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.dct.kafka.builder.ConsumerBuilder;
import br.unicamp.dct.kafka.TopicConfigProvider;
import br.unicamp.dct.kafka.builder.ProducerBuilder;
import br.unicamp.dct.kafka.config.TopicConfig;
import br.unicamp.dct.thread.MemoryContentReaderThread;
import br.unicamp.dct.thread.MemoryContentWriterThread;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DistributedMemory implements Memory {

    private String name;
    private String brokers;
    private List<TopicConfig> topicsConfig;
    private DistributedMemoryType type;
    private List<Memory> memories;
    private List<MemoryContentWriterThread> memoryContentWriterThreads;
    private List<MemoryContentReaderThread> memoryContentReaderThreads;

    private Logger logger;

    public DistributedMemory(String name, String brokers, DistributedMemoryType type, List<TopicConfig> topicsConfig) {
        memorySetup(name, brokers, type, topicsConfig);
    }

    private void memorySetup(String name, String brokers, DistributedMemoryType type,
                             List<TopicConfig> topics) {
        this.name = name;
        this.topicsConfig = topics;
        this.type = type;
        this.brokers = brokers;

        this.memories = new ArrayList<>();
        this.memoryContentWriterThreads = new ArrayList<>();
        this.memoryContentReaderThreads = new ArrayList<>();

        this.logger = LoggerFactory.getLogger(DistributedMemory.class);

        if (type == DistributedMemoryType.INPUT_MEMORY) {
            generateConsumers(topics);
        } else
            generateProducers(topics);
    }

    private void generateConsumers(List<TopicConfig> topics) {
        topics.forEach(topic -> {
            final KafkaConsumer<String, String> consumer =
                    ConsumerBuilder.buildConsumer(brokers, name);

            if(topic.getPrefix() != null) {
                if(!topic.getPrefix().isEmpty()) {
                    final List<TopicConfig> foundTopics = TopicConfigProvider.generateTopicConfigsPrefix(brokers, topic.getPrefix());
                    generateConsumers(foundTopics);

                    return;
                }
            }

            consumer.subscribe(Collections.singletonList(topic.getName()));

            final Memory memory = createMemoryObject(String.format("%s_DM", topic.getName()));
            getMemories().add(memory);

            MemoryContentWriterThread memoryContentWriterThread = new MemoryContentWriterThread(memory, consumer, topic, topic.getClassToConvert());
            memoryContentWriterThread.start();

            getMemoryWriterThreads().add(memoryContentWriterThread);
        });
    }

    private void generateProducers(List<TopicConfig> topics) {
        topics.forEach(topicConfig -> {
            final KafkaProducer<String, String> producer =
                    ProducerBuilder.buildProducer(brokers);

            final Memory memory = createMemoryObject(String.format("%s_DM", topicConfig.getName()));
            getMemories().add(memory);

            MemoryContentReaderThread memoryContentReaderThread = new MemoryContentReaderThread(memory, producer, topicConfig);
            memoryContentReaderThread.start();

            getMemoryReaderThreads().add(memoryContentReaderThread);
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
            int i = memories.get(0).setI(info);
            notifyReaderThread(0);

            return i;
        } catch (IndexOutOfBoundsException ex) {
            logger.error("Impossible to set memory content. Index 0 out of bounds.");
            return -1;
        }
    }

    public int setI(Object info, int index) {
        try {
            int i = memories.get(index).setI(info);
            notifyReaderThread(index);

            return i;
        } catch (IndexOutOfBoundsException ex) {
            logger.error(String.format("Impossible to set memory content. Index %s out of bounds.", index));
            return -1;
        }
    }

    public int setI(Object info, double evaluation, int index) {
        try {
            memories.get(index).setEvaluation(evaluation);

            int i = memories.get(index).setI(info);
            notifyReaderThread(index);

            return i;
        } catch (IndexOutOfBoundsException ex) {
            logger.error(String.format("Impossible to set memory content. Index %s out of bounds.", index));
            return -1;
        }
    }

    private void notifyReaderThread(int index) {
        if(type == DistributedMemoryType.OUTPUT_MEMORY
                && topicsConfig.get(index).getDistributedMemoryBehavior() == DistributedMemoryBehavior.TRIGGERED) {
            memoryContentReaderThreads.get(index).notify();
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
            notifyReaderThread(0);
        } catch (IndexOutOfBoundsException ex) {
            logger.error("Impossible to set memory evaluation. Index 0 out of bounds.");
        }
    }

    public void setEvaluation(Double evaluation, int index) {
        try {
            memories.get(index).setEvaluation(evaluation);
            notifyReaderThread(index);
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

    public List<TopicConfig> getTopicsConfig() {
        return topicsConfig;
    }

    public List<MemoryContentWriterThread> getMemoryWriterThreads() {
        return memoryContentWriterThreads;
    }

    public List<MemoryContentReaderThread> getMemoryReaderThreads() {
        return memoryContentReaderThreads;
    }
}
