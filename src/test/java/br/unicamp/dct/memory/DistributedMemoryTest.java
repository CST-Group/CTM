package br.unicamp.dct.memory;

import com.google.gson.Gson;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class DistributedMemoryTest {

    private DistributedMemory distributedInputMemory;
    private DistributedMemory distributedOutputMemory;
    private List<TopicConfig> inputTopicConfigs;
    private List<TopicConfig> outputTopicConfigs;
    private Gson gson;

    @Before
    public void setUp() throws InterruptedException {
        inputTopicConfigs = new ArrayList<>();
        outputTopicConfigs = new ArrayList<>();
        inputTopicConfigs.add(new TopicConfig("topic-2", DistributedMemoryBehavior.PULLED));
        outputTopicConfigs.add(new TopicConfig("topic-2", DistributedMemoryBehavior.PULLED));
        gson = new Gson();

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
        String message = "Test message in the distributed memory!";
        distributedOutputMemory.setI(message);

        Thread.sleep(20);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());

        message = "New message to test!";
        distributedOutputMemory.setI(message);

        Thread.sleep(20);
        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());
    }

//    @Test
//    public void shouldHasMessageInInputDistributedMemoryUsingPrefix() {
//        String message = "Test message in the distributed memory!";
//        distributedOutputMemory.setI(message);
//
//        Assert.assertEquals(message, distributedInputMemory.getI());
//
//        message = "New message to test!";
//        distributedOutputMemory.setI(message);
//
//        Assert.assertEquals(message, distributedInputMemory.getI());
//    }
}
