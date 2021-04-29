package br.unicamp.dct.memory;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.dct.kafka.builder.ConsumerBuilder;
import br.unicamp.dct.kafka.builder.ProducerBuilder;
import br.unicamp.dct.kafka.config.TopicConfig;
import br.unicamp.dct.thread.MemoryContentPublisherThread;
import br.unicamp.dct.thread.MemoryContentReceiverThread;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class DistributedMemory implements Memory {

    private String name;
    private String brokers;
    private List<TopicConfig> topicsConfig;
    private DistributedMemoryType type;
    private List<Memory> memories;
    private List<MemoryContentReceiverThread> memoryContentReceiverThreads;
    private List<MemoryContentPublisherThread> memoryContentPublisherThreads;

    private final Logger logger = LoggerFactory.getLogger(DistributedMemory.class);

    public DistributedMemory(String name, DistributedMemoryType type, List<TopicConfig> topicsConfig) {
        memorySetup(name, type, topicsConfig);
    }

    private void memorySetup(String name, DistributedMemoryType type,
                             List<TopicConfig> topics) {
        this.name = name;
        this.topicsConfig = topics;
        this.type = type;

        this.memories = new ArrayList<>();
        this.memoryContentReceiverThreads = new ArrayList<>();
        this.memoryContentPublisherThreads = new ArrayList<>();
    }

    public void initMemory(String brokers) {
        this.setBrokers(brokers);

        if (getType() == DistributedMemoryType.INPUT_MEMORY) {
            consumersSetup(this.topicsConfig);
        } else
            producersSetup(this.topicsConfig);
    }

    private void consumersSetup(List<TopicConfig> topics) {
        Map<TopicConfig, KafkaConsumer<String, String>> topicConsumersMap = ConsumerBuilder.generateConsumers(topics, getBrokers(), name);

        topicConsumersMap.forEach((topicConfig, consumer) -> {
            String topicName = topicConfig.getName();

            final Memory memory = MemoryBuilder.createMemoryObject(String.format("%s_DM", topicName));
            getMemories().add(memory);

            MemoryContentReceiverThread memoryContentReceiverThread = new MemoryContentReceiverThread(memory, consumer, topicConfig);
            memoryContentReceiverThread.start();

            getMemoryWriterThreads().add(memoryContentReceiverThread);
        });
    }

    private void producersSetup(List<TopicConfig> topics) {
        List<KafkaProducer<String, String>> producers = ProducerBuilder.generateProducers(topics, getBrokers());

        for (int i = 0; i < producers.size(); i++) {
            final Memory memory = MemoryBuilder.createMemoryObject(String.format("%s_DM", topics.get(i).getName()));
            getMemories().add(memory);

            MemoryContentPublisherThread memoryContentPublisherThread = new MemoryContentPublisherThread(memory, producers.get(i), topics.get(i));
            memoryContentPublisherThread.start();

            getMemoryReaderThreads().add(memoryContentPublisherThread);
        }
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
        if (getType() == DistributedMemoryType.OUTPUT_MEMORY
                && topicsConfig.get(index).getDistributedMemoryBehavior() == DistributedMemoryBehavior.TRIGGERED) {
            Memory memory = memories.get(index);
            synchronized (memory) {
                memory.notify();
            }
        }
    }

    @Override
    public synchronized Double getEvaluation() {
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
        if (getType() == DistributedMemoryType.INPUT_MEMORY) {
            consumersSetup(Collections.singletonList(topic));
        } else {
            producersSetup(Collections.singletonList(topic));
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

    public List<MemoryContentReceiverThread> getMemoryWriterThreads() {
        return memoryContentReceiverThreads;
    }

    public List<MemoryContentPublisherThread> getMemoryReaderThreads() {
        return memoryContentPublisherThreads;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public DistributedMemoryType getType() {
        return type;
    }

    public void setType(DistributedMemoryType type) {
        this.type = type;
    }
}
