package br.unicamp.dct.memory;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DistributedMemoryTest {

    private DistributedMemory distributedInputMemory;
    private DistributedMemory distributedOutputMemory;
    private List<TopicConfig> inputTopicConfigs;
    private List<TopicConfig> outputTopicConfigs;

    @Before
    public void setUp() {
        inputTopicConfigs = new ArrayList<>();
        outputTopicConfigs = new ArrayList<>();
        inputTopicConfigs.add(new TopicConfig("topic-1", DistributedMemoryBehavior.PULLED));
        outputTopicConfigs.add(new TopicConfig("topic-1", DistributedMemoryBehavior.PULLED));

        distributedInputMemory = new DistributedMemory(
                "MEMORY_TEST",
                "http://localhost:9092",
                DistributedMemoryType.INPUT_MEMORY,
                inputTopicConfigs
        );

        distributedOutputMemory = new DistributedMemory(
                "MEMORY_TEST",
                "http://localhost:9092",
                DistributedMemoryType.OUTPUT_MEMORY,
                outputTopicConfigs
        );
    }

    @Test
    public void shouldHasMessageInInputDistributedMemory() {
        String message = "Test message in the distributed memory!";
        distributedOutputMemory.setI(message);

        Assert.assertEquals(message, distributedOutputMemory.getI());

        message = "New message to test!";
        distributedOutputMemory.setI(message);

        Assert.assertEquals(message, distributedOutputMemory.getI());
    }

    @Test
    public void shouldHasMessageInInputDistributedMemoryUsingPrefix() {
        String message = "Test message in the distributed memory!";
        distributedOutputMemory.setI(message);

        Assert.assertEquals(message, distributedOutputMemory.getI());

        message = "New message to test!";
        distributedOutputMemory.setI(message);

        Assert.assertEquals(message, distributedOutputMemory.getI());
    }
}
