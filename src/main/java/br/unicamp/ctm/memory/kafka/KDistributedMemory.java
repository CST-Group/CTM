package br.unicamp.ctm.memory.kafka;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObserver;
import br.unicamp.ctm.memory.DistributedMemory;
import br.unicamp.ctm.memory.DistributedMemoryType;
import br.unicamp.ctm.memory.kafka.builder.KConsumerBuilder;
import br.unicamp.ctm.memory.kafka.builder.KProducerBuilder;
import br.unicamp.ctm.memory.kafka.config.TopicConfig;
import br.unicamp.ctm.memory.MemoryBuilder;
import br.unicamp.ctm.memory.kafka.thread.KMemoryContentPublisherThread;
import br.unicamp.ctm.memory.kafka.thread.KMemoryContentReceiverThread;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import java.util.*;
import org.apache.log4j.Logger;

public class KDistributedMemory implements Memory, DistributedMemory {

    private String name;
    private List<TopicConfig> topicsConfig;
    private DistributedMemoryType type;
    private List<Memory> memories;
    private List<KMemoryContentReceiverThread> kMemoryContentReceiverThreads;
    private List<KMemoryContentPublisherThread> kMemoryContentPublisherThreads;

    private final Logger logger = Logger.getLogger(KDistributedMemory.class);

    public KDistributedMemory(String name, DistributedMemoryType type, List<TopicConfig> topicsConfig) {
        memorySetup(name, type, topicsConfig);
    }

    private void memorySetup(String name, DistributedMemoryType type,
                             List<TopicConfig> topics) {
        this.name = name;
        this.topicsConfig = topics;
        this.type = type;

        logger.info(String.format("Creating KDistributeMemory %s for type %s.", name, type));

        this.memories = new ArrayList<>();
        this.kMemoryContentReceiverThreads = new ArrayList<>();
        this.kMemoryContentPublisherThreads = new ArrayList<>();

        initMemory();

        logger.info(String.format("KDistributeMemory %s created.", name, type));

    }

    public void initMemory() {

        if (getType() == DistributedMemoryType.INPUT_MEMORY || getType() == DistributedMemoryType.INPUT_BROADCAST_MEMORY) {
            consumersSetup(this.topicsConfig);
        } else
            producersSetup(this.topicsConfig);

    }

    private void consumersSetup(List<TopicConfig> topics) {
        logger.info("Creating the consumers.");

        Map<TopicConfig, KafkaConsumer<String, String>> topicConsumersMap = KConsumerBuilder.generateConsumers(topics, name);

        topicConsumersMap.forEach((topicConfig, consumer) -> {

            String topicName = topicConfig.getName();

            final Memory memory = MemoryBuilder.createMemoryObject(topicName);
            getMemories().add(memory);

            KMemoryContentReceiverThread KMemoryContentReceiverThread = new KMemoryContentReceiverThread(memory, consumer, topicConfig);
            KMemoryContentReceiverThread.start();

            getMemoryWriterThreads().add(KMemoryContentReceiverThread);
        });

        logger.info("Consumers created.");
    }

    private void producersSetup(List<TopicConfig> topics) {

        logger.info("Creating the producers.");

        List<KafkaProducer<String, String>> producers = KProducerBuilder.generateProducers(topics);

        for (int i = 0; i < producers.size(); i++) {
            final Memory memory = MemoryBuilder.createMemoryObject(topics.get(i).getName());
            getMemories().add(memory);

            KMemoryContentPublisherThread KMemoryContentPublisherThread = new KMemoryContentPublisherThread(memory, producers.get(i), topics.get(i));
            KMemoryContentPublisherThread.start();

            getMemoryReaderThreads().add(KMemoryContentPublisherThread);
        }

        logger.info("Producers created.");
    }

    @Override
    public Object getI() {
        final Memory memory = memories.stream().max(Comparator.comparing(Memory::getEvaluation)).orElse(null);
        return memory != null ? memory.getI() : null;
    }

    public Memory getMemory() {
        final Memory memory = memories.stream().max(Comparator.comparing(Memory::getEvaluation)).orElse(null);
        return memory;
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
                && topicsConfig.get(index).getDistributedMemoryBehavior() == KDistributedMemoryBehavior.TRIGGERED) {
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

    @Override
    public void addMemoryObserver(MemoryObserver memoryObserver) {

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

    @Override
    public void setType(String type) {
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<Memory> getMemories() {
        return memories;
    }

    public List<TopicConfig> getTopicsConfig() {
        return topicsConfig;
    }

    public List<KMemoryContentReceiverThread> getMemoryWriterThreads() {
        return kMemoryContentReceiverThreads;
    }

    public List<KMemoryContentPublisherThread> getMemoryReaderThreads() {
        return kMemoryContentPublisherThreads;
    }

    @Override
    public DistributedMemoryType getType() {
        return type;
    }

    public void setType(DistributedMemoryType type) {
        this.type = type;
    }
}
