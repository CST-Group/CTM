package br.unicamp.dct.memory;

public class TopicConfig {

    private String name;
    private DistributedMemoryBehavior distributedMemoryBehavior;
    private String prefix;

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
    }

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior, String prefix) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
