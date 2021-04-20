package br.unicamp.dct.memory.integration;

import br.unicamp.dct.memory.DistributedMemory;
import br.unicamp.dct.memory.DistributedMemoryBehavior;
import br.unicamp.dct.memory.DistributedMemoryType;
import br.unicamp.dct.memory.TopicConfig;
import com.google.gson.Gson;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DistributedMemoryIntegrationTest {

    private DistributedMemory distributedInputMemory;
    private DistributedMemory distributedOutputMemory;
    private List<TopicConfig> inputTopicConfigs;
    private List<TopicConfig> outputTopicConfigs;
    private Gson gson;

    @Before
    public void setUp() throws InterruptedException {
        inputTopicConfigs = new ArrayList<>();
        outputTopicConfigs = new ArrayList<>();
        gson = new Gson();
    }

    private void setUpDistributedMemories() throws InterruptedException {
        inputTopicConfigs.add(new TopicConfig("topic-2", DistributedMemoryBehavior.PULLED));
        outputTopicConfigs.add(new TopicConfig("topic-2", DistributedMemoryBehavior.PULLED));

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

        Thread.sleep(5000);
    }

    private void setUpDistributedMemoriesByPrefix() throws InterruptedException {
        inputTopicConfigs.add(new TopicConfig("topic-2", DistributedMemoryBehavior.PULLED, "topic-*"));
        outputTopicConfigs.add(new TopicConfig("topic-2", DistributedMemoryBehavior.PULLED));

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

        Thread.sleep(5000);
    }


    @Test
    public void shouldHasMessageInInputDistributedMemory() throws InterruptedException {

        setUpDistributedMemories();

        String message = "Test message in the distributed memory!";

        distributedOutputMemory.setI(message);

        Thread.sleep(20);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());

        message = "New message to test!";
        distributedOutputMemory.setI(message);

        Thread.sleep(20);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());
    }

    @Test
    public void shouldHasMessageInInputDistributedMemoryUsingPrefix() throws InterruptedException {

        setUpDistributedMemoriesByPrefix();

        String message = "Test message in the distributed memory!";

        distributedOutputMemory.setI(message);

        Thread.sleep(20);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());

        message = "New message to test!";
        distributedOutputMemory.setI(message);

        Thread.sleep(20);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());
    }


}
