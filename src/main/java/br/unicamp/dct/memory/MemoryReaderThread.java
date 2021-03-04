package br.unicamp.dct.memory;

import br.unicamp.cst.core.entities.Memory;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MemoryReaderThread extends Thread {

    private Memory memory;
    private Object lastI;
    private KafkaProducer<String, String> kafkaProducer;
    private Gson gson;
    private DistributedMemoryBehavior distributedMemoryBehavior;

    public MemoryReaderThread(Memory memory, KafkaProducer<String, String> kafkaProducer, DistributedMemoryBehavior distributedMemoryBehavior) {
        this.memory = memory;
        this.kafkaProducer = kafkaProducer;
        this.distributedMemoryBehavior = distributedMemoryBehavior;

        this.gson = new Gson();
    }

    @Override
    public void run() {
        while (true) {
            if(distributedMemoryBehavior == DistributedMemoryBehavior.TRIGGERED) {
                if(memory.getI() != lastI) {
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(memory.getName().replace("_MD", ""), gson.toJson(memory));
                    kafkaProducer.send(record);

                    lastI = memory.getI();
                }
            } else {
                try {
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(memory.getName().replace("_MD", ""), gson.toJson(memory));
                    kafkaProducer.send(record);

                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
