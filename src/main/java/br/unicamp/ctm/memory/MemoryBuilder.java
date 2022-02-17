package br.unicamp.ctm.memory;

import br.unicamp.cst.core.entities.MemoryObject;

public class MemoryBuilder {

    public static MemoryObject createMemoryObject(String name) {
        MemoryObject memoryObject = new MemoryObject();
        memoryObject.setTimestamp(System.currentTimeMillis());
        memoryObject.setEvaluation(0.0d);
        memoryObject.setType(name);

        return memoryObject;
    }
}
