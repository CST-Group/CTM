package br.unicamp.ctm.codelet;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.ctm.memory.DistributedMemory;
import br.unicamp.ctm.memory.DistributedMemoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeletApplication {

    private List<Codelet> codelets;
    private Map<Codelet, List<Memory>> codeletMemories;

    public CodeletApplication(
            Map<Codelet, List<Memory>> codeletMemories
    ) {
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
                if(memory instanceof DistributedMemory) {
                    DistributedMemory distributedMemory = (DistributedMemory) memory;

                    if (distributedMemory.getType() == DistributedMemoryType.INPUT_MEMORY) {
                        if(codelet.getInputs().stream().noneMatch(inputMemory -> inputMemory == distributedMemory)) {
                            codelet.addInput(memory);
                        }
                    } else {
                        if (distributedMemory.getType() == DistributedMemoryType.OUTPUT_MEMORY) {
                            if(codelet.getOutputs().stream().noneMatch(outputMemory -> outputMemory == distributedMemory)) {
                                codelet.addOutput(memory);
                            }
                        } else {
                            if(codelet.getBroadcast().stream().noneMatch(broadcastMemory -> broadcastMemory == distributedMemory)) {
                                codelet.addBroadcast(memory);
                            }
                        }
                    }
                }
            });

            getCodelets().add(codelet);
            codelet.start();
        });
    }

    public Map<Codelet, List<Memory>> getCodeletMemories() {
        return codeletMemories;
    }

    public void setCodeletMemories(Map<Codelet, List<Memory>> codeletMemories) {
        this.codeletMemories = codeletMemories;
    }

    public List<Codelet> getCodelets() {
        return codelets;
    }
}
