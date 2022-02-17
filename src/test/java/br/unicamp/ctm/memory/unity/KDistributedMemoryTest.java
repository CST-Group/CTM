package br.unicamp.ctm.memory.unity;

import br.unicamp.ctm.memory.kafka.KDistributedMemory;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KDistributedMemoryTest {

    @Mock
    KDistributedMemory distributedInputMemory;

    @Mock
    KDistributedMemory distributedOutputMemory;

    @Test
    public void shouldHasMessageInInputDistributedMemory() throws InterruptedException {
        String message = "Test message in the distributed memory!";

        when(distributedInputMemory.getI()).thenReturn(message);
        when(distributedOutputMemory.getI()).thenReturn(message);

        distributedOutputMemory.setI(message);

        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());

        distributedOutputMemory.setI(message);

        Assert.assertEquals(distributedOutputMemory.getI(), distributedInputMemory.getI());
    }
}
