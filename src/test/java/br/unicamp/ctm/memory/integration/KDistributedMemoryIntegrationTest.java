package br.unicamp.ctm.memory.integration;

import br.unicamp.ctm.memory.kafka.KDistributedMemory;
import br.unicamp.ctm.memory.kafka.KDistributedMemoryBehavior;
import br.unicamp.ctm.memory.DistributedMemoryType;
import br.unicamp.ctm.memory.kafka.config.TopicConfig;
import junit.framework.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KDistributedMemoryIntegrationTest {

    private KDistributedMemory distributedInputMemory;
    private KDistributedMemory distributedOutputMemory;
    private List<TopicConfig> inputTopicConfigs;
    private List<TopicConfig> outputTopicConfigs;

    private void setUpDistributedMemories() throws InterruptedException {
        inputTopicConfigs = new ArrayList<>();
        outputTopicConfigs = new ArrayList<>();

        inputTopicConfigs.add(new TopicConfig("http://localhost:9092","topic-1", KDistributedMemoryBehavior.PULLED));
        outputTopicConfigs.add(new TopicConfig("http://localhost:9092", "topic-1", KDistributedMemoryBehavior.TRIGGERED));

        distributedInputMemory = new KDistributedMemory(
                "MEMORY_TEST_INPUT",
                DistributedMemoryType.INPUT_MEMORY,
                inputTopicConfigs
        );

        distributedOutputMemory = new KDistributedMemory(
                "MEMORY_TEST_OUTPUT",
                DistributedMemoryType.OUTPUT_MEMORY,
                outputTopicConfigs
        );

        Thread.sleep(1000);
    }

    private void setUpDistributedMemoriesByPrefix() throws InterruptedException {
        inputTopicConfigs = new ArrayList<>();
        outputTopicConfigs = new ArrayList<>();

        inputTopicConfigs.add(new TopicConfig("http://localhost:9092", KDistributedMemoryBehavior.PULLED, "topic-*"));
        outputTopicConfigs.add(new TopicConfig("http://localhost:9092", "topic-1", KDistributedMemoryBehavior.TRIGGERED));

        distributedInputMemory = new KDistributedMemory(
                "MEMORY_TEST_INPUT",
                DistributedMemoryType.INPUT_MEMORY,
                inputTopicConfigs
        );

        distributedOutputMemory = new KDistributedMemory(
                "MEMORY_TEST_OUTPUT",
                DistributedMemoryType.OUTPUT_MEMORY,
                outputTopicConfigs
        );

        Thread.sleep(1000);
    }

    private void setUpDistributedMemoriesWithClassConvert() throws InterruptedException {
        inputTopicConfigs = new ArrayList<>();
        outputTopicConfigs = new ArrayList<>();

        inputTopicConfigs.add(new TopicConfig("http://localhost:9092", "topic-3", KDistributedMemoryBehavior.PULLED, Integer.class.getName()));
        outputTopicConfigs.add(new TopicConfig("http://localhost:9092","topic-3", KDistributedMemoryBehavior.TRIGGERED));

        distributedInputMemory = new KDistributedMemory(
                "MEMORY_TEST_INPUT",
                DistributedMemoryType.INPUT_MEMORY,
                inputTopicConfigs
        );

        distributedOutputMemory = new KDistributedMemory(
                "MEMORY_TEST_OUTPUT",
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

        Thread.sleep(2000);

        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());

        message = "New message to test!";
        distributedOutputMemory.setI(message);

        Thread.sleep(2000);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());
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
