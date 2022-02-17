package br.unicamp.ctm.memory.kafka.config;

import br.unicamp.ctm.memory.kafka.KDistributedMemoryBehavior;

public class TopicConfig {

    private String name;
    private String broker;
    private KDistributedMemoryBehavior kDistributedMemoryBehavior;
    private String regexPattern;
    private String className;

    public TopicConfig(String name, String broker, KDistributedMemoryBehavior kDistributedMemoryBehavior) {
        this.setName(name);
        this.setDistributedMemoryBehavior(kDistributedMemoryBehavior);
        this.setClassName(null);
        this.setBroker(broker);
    }

    public TopicConfig(String name, String broker, KDistributedMemoryBehavior kDistributedMemoryBehavior, String className) {
        this.setName(name);
        this.setDistributedMemoryBehavior(kDistributedMemoryBehavior);
        this.setClassName(className);
        this.setBroker(broker);
    }

    public TopicConfig(String broker, KDistributedMemoryBehavior kDistributedMemoryBehavior, String regexPattern) {
        this.setDistributedMemoryBehavior(kDistributedMemoryBehavior);
        this.setRegexPattern(regexPattern);
        this.setClassName(null);
        this.setBroker(broker);
    }

    public TopicConfig(String broker, KDistributedMemoryBehavior kDistributedMemoryBehavior, String regexPattern, String className) {
        this.setDistributedMemoryBehavior(kDistributedMemoryBehavior);
        this.setRegexPattern(regexPattern);
        this.setClassName(className);
        this.setBroker(broker);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KDistributedMemoryBehavior getDistributedMemoryBehavior() {
        return kDistributedMemoryBehavior;
    }

    public void setDistributedMemoryBehavior(
        KDistributedMemoryBehavior kDistributedMemoryBehavior) {
        this.kDistributedMemoryBehavior = kDistributedMemoryBehavior;
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }
}
