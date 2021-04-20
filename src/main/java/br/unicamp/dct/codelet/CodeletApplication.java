package br.unicamp.dct.codelet;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.dct.memory.DistributedMemory;
import br.unicamp.dct.memory.DistributedMemoryType;
import br.unicamp.dct.kafka.config.TopicConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeletApplication {

    private String brokers;
    private List<Codelet> codelets;
    private List<DistributedMemory> distributedMemories;
    private Map<Codelet, List<TopicConfig>> consumerCodeletTopics;
    private Map<Codelet, List<TopicConfig>> producerCodeletTopics;

    public CodeletApplication(
            String brokers,
            List<Codelet> codelets,
            Map<Codelet, List<TopicConfig>> consumerCodeletTopics,
            Map<Codelet, List<TopicConfig>> producerCodeletTopics
    ) {
        this.brokers = brokers;
        this.codelets = codelets;
        this.consumerCodeletTopics = consumerCodeletTopics;
        this.producerCodeletTopics = producerCodeletTopics;
        this.distributedMemories = new ArrayList<>();

        setupCodeletApplication();
    }

    private void setupCodeletApplication() {
        initializeDistributedMemories();
        startCodelets();
    }

    private void startCodelets() {
        getCodelets().forEach(Codelet::start);
    }
    
    private void initializeDistributedMemories() {
        getConsumerCodeletTopics().forEach((codelet, topics) -> {
            DistributedMemory distributedMemory =
                    new DistributedMemory("INPUT_DISTRIBUTED_MEMORY", getBrokers(), DistributedMemoryType.INPUT_MEMORY, topics);

            getDistributedMemories().add(distributedMemory);

            codelet.addInput(distributedMemory);

            getCodelets().add(codelet);
        });

        getProducerCodeletTopics().forEach((codelet, topics) -> {
            DistributedMemory distributedMemory =
                    new DistributedMemory("OUTPUT_DISTRIBUTED_MEMORY", getBrokers(), DistributedMemoryType.OUTPUT_MEMORY, topics);

            getDistributedMemories().add(distributedMemory);

            codelet.addOutput(distributedMemory);

            if (getCodelets().stream().noneMatch(cod -> cod == codelet))
                getCodelets().add(codelet);
        });
    }

    public String getBrokers() {
        return brokers;
    }

    public List<Codelet> getCodelets() {
        return codelets;
    }

    public List<DistributedMemory> getDistributedMemories() {
        return distributedMemories;
    }

    public Map<Codelet, List<TopicConfig>> getConsumerCodeletTopics() {
        return consumerCodeletTopics;
    }

    public Map<Codelet, List<TopicConfig>> getProducerCodeletTopics() {
        return producerCodeletTopics;
    }
}
