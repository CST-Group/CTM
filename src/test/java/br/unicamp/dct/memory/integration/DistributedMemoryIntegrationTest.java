package br.unicamp.dct.memory.integration;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.dct.memory.DistributedMemory;
import br.unicamp.dct.memory.DistributedMemoryBehavior;
import br.unicamp.dct.memory.DistributedMemoryType;
import br.unicamp.dct.kafka.config.TopicConfig;
import junit.framework.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DistributedMemoryIntegrationTest {

    private DistributedMemory distributedInputMemory;
    private DistributedMemory distributedOutputMemory;
    private List<TopicConfig> inputTopicConfigs;
    private List<TopicConfig> outputTopicConfigs;

    private void setUpDistributedMemories() throws InterruptedException {
        inputTopicConfigs = new ArrayList<>();
        outputTopicConfigs = new ArrayList<>();

        inputTopicConfigs.add(new TopicConfig("topic-1", DistributedMemoryBehavior.PULLED));
        outputTopicConfigs.add(new TopicConfig("topic-1", DistributedMemoryBehavior.TRIGGERED));

        distributedInputMemory = new DistributedMemory(
                "MEMORY_TEST_INPUT",
                "http://localhost:9092",
                DistributedMemoryType.INPUT_MEMORY,
                inputTopicConfigs
        );

        distributedOutputMemory = new DistributedMemory(
                "MEMORY_TEST_OUTPUT",
                "http://localhost:9092",
                DistributedMemoryType.OUTPUT_MEMORY,
                outputTopicConfigs
        );

        Thread.sleep(1000);
    }

    private void setUpDistributedMemoriesByPrefix() throws InterruptedException {
        inputTopicConfigs = new ArrayList<>();
        outputTopicConfigs = new ArrayList<>();

        inputTopicConfigs.add(new TopicConfig(DistributedMemoryBehavior.PULLED, "topic-*"));
        outputTopicConfigs.add(new TopicConfig("topic-1", DistributedMemoryBehavior.TRIGGERED));

        distributedInputMemory = new DistributedMemory(
                "MEMORY_TEST_INPUT",
                "http://localhost:9092",
                DistributedMemoryType.INPUT_MEMORY,
                inputTopicConfigs
        );

        distributedOutputMemory = new DistributedMemory(
                "MEMORY_TEST_OUTPUT",
                "http://localhost:9092",
                DistributedMemoryType.OUTPUT_MEMORY,
                outputTopicConfigs
        );

        Thread.sleep(1000);
    }

    private void setUpDistributedMemoriesWithClassConvert() throws InterruptedException {
        inputTopicConfigs = new ArrayList<>();
        outputTopicConfigs = new ArrayList<>();

        inputTopicConfigs.add(new TopicConfig("topic-3", DistributedMemoryBehavior.PULLED, Integer.class.getName()));
        outputTopicConfigs.add(new TopicConfig("topic-3", DistributedMemoryBehavior.TRIGGERED));

        distributedInputMemory = new DistributedMemory(
                "MEMORY_TEST_INPUT",
                "http://localhost:9092",
                DistributedMemoryType.INPUT_MEMORY,
                inputTopicConfigs
        );

        distributedOutputMemory = new DistributedMemory(
                "MEMORY_TEST_OUTPUT",
                "http://localhost:9092",
                DistributedMemoryType.OUTPUT_MEMORY,
                outputTopicConfigs
        );

        Thread.sleep(1000);
    }


    @Test
    public void shouldHasMessageInInputDistributedMemory() throws InterruptedException {
        setUpDistributedMemories();

        String message = "Test message in the distributed memory!";

        distributedOutputMemory.setI(message);

        Thread.sleep(1000);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());

        message = "New message to test!";
        distributedOutputMemory.setI(message);

        Thread.sleep(1000);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());
    }

    @Test
    public void shouldHasMessageInInputDistributedMemoryUsingPrefix() throws InterruptedException {
        setUpDistributedMemoriesByPrefix();

        String message = "Test message in the distributed memory!";

        distributedOutputMemory.setI(message);

        Thread.sleep(1000);

        for (Memory testMemory: distributedInputMemory.getMemories()) {
            System.out.println(testMemory.getName());
            System.out.println(testMemory.getI());
        }

        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI(0));

        message = "New message to test!";
        distributedOutputMemory.setI(message);

        Thread.sleep(1000);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI(0));
    }


    @Test
    public void shouldHasMessageInInputDistributedMemoryWithClassConverter() throws InterruptedException {
        setUpDistributedMemoriesWithClassConvert();

        String message = "1";
        distributedOutputMemory.setI(message);

        Thread.sleep(1000);
        Assert.assertEquals(1, distributedInputMemory.getI());

        message = "2";
        distributedOutputMemory.setI(message);

        Thread.sleep(1000);
        Assert.assertEquals(2, distributedInputMemory.getI());
    }
}
