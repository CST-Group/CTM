package br.unicamp.dct.memory;

public class TopicConfig {

    private String name;
    private DistributedMemoryBehavior distributedMemoryBehavior;
    private String prefix;
    private Class classToConvert;

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setClassToConvert(null);
    }

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior, String prefix) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setPrefix(prefix);
        this.setClassToConvert(null);
    }

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior, Class classToConvert) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setClassToConvert(classToConvert);
    }

    public TopicConfig(String name, DistributedMemoryBehavior distributedMemoryBehavior, String prefix, Class classToConvert) {
        this.setName(name);
        this.setDistributedMemoryBehavior(distributedMemoryBehavior);
        this.setPrefix(prefix);
        this.setClassToConvert(classToConvert);
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

    public Class getClassToConvert() {
        return classToConvert;
    }

    public void setClassToConvert(Class classToConvert) {
        this.classToConvert = classToConvert;
    }
}
