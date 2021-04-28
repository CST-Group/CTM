package br.unicamp.dct.kafka.config;

import br.unicamp.dct.memory.DistributedMemoryBehavior;

public class TopicConfig {

    private String name;
    private DistributedMemoryBehavior distributedMemoryBehavior;
    private String prefix;
    private String className;

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setClassName(null);
    }

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior, String className) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setClassName(className);
    }

    public TopicConfig(DistributedMemoryBehavior distributedMemoryBehavior, String prefix) {
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setPrefix(prefix);
        this.setClassName(null);
    }

    public TopicConfig(DistributedMemoryBehavior distributedMemoryBehavior, String prefix, String className) {
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setPrefix(prefix);
        this.setClassName(className);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DistributedMemoryBehavior getDistributedMemoryBehavior() {
        return distributedMemoryBehavior;
    }

    public void setDistributedMemoryBehavior(DistributedMemoryBehavior distributedMemoryBehavior) {
        this.distributedMemoryBehavior = distributedMemoryBehavior;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
