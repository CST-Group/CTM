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
    private Map<Codelet, List<DistributedMemory>> codeletMemories;

    public CodeletApplication(
            String brokers,
            Map<Codelet, List<DistributedMemory>> codeletMemories
    ) {
        this.setBrokers(brokers);
        this.setCodeletMemories(codeletMemories);
        this.codelets = new ArrayList<>();

        setupCodeletApplication();
    }

    private void setupCodeletApplication() {
        initializeDistributedMemories();
    }
    
    private void initializeDistributedMemories() {
        getCodeletMemories().forEach((codelet, memories) -> {
            memories.forEach(memory -> {
                memory.initMemory(getBrokers());

                if (memory.getType() == DistributedMemoryType.INPUT_MEMORY) {
                    codelet.addInput(memory);
                } else {
                    codelet.addOutput(memory);
                }
            });

            getCodelets().add(codelet);
            codelet.start();
        });
    }

    public String getBrokers() {
        return brokers;
    }


    public Map<Codelet, List<DistributedMemory>> getCodeletMemories() {
        return codeletMemories;
    }

    public void setCodeletMemories(Map<Codelet, List<DistributedMemory>> codeletMemories) {
        this.codeletMemories = codeletMemories;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public List<Codelet> getCodelets() {
        return codelets;
    }
}
