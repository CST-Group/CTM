package br.unicamp.dct.memory;

public class TopicConfig {

    private String name;
    private DistributedMemoryBehavior distributedMemoryBehavior;
    private DistributedMemoryType distributedMemoryType;
    private String prefix;

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior, DistributedMemoryType distributedMemoryType) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setDistributedMemoryType(distributedMemoryType);
    }

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior, DistributedMemoryType distributedMemoryType, String prefix) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setDistributedMemoryType(distributedMemoryType);
        this.setPrefix(prefix);
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

    public DistributedMemoryType getDistributedMemoryType() {
        return distributedMemoryType;
    }

    public void setDistributedMemoryType(DistributedMemoryType distributedMemoryType) {
        this.distributedMemoryType = distributedMemoryType;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
