package br.unicamp.dct.memory;

import br.unicamp.cst.core.entities.Memory;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MemoryReaderThread extends Thread {

    private final Memory memory;
    private Object lastI;
    private final KafkaProducer<String, String> kafkaProducer;
    private final Gson gson;
    private final TopicConfig topicConfig;

    public MemoryReaderThread(Memory memory, KafkaProducer<String, String> kafkaProducer, TopicConfig topicConfig) {
        this.memory = memory;
        this.kafkaProducer = kafkaProducer;
        this.topicConfig = topicConfig;

        this.gson = new Gson();
    }

    @Override
    public void run() {
        while (true) {
            if(topicConfig.getDistributedMemoryBehavior() == DistributedMemoryBehavior.TRIGGERED) {
                if(memory.getI() != lastI) {
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicConfig.getName(), gson.toJson(memory));
                    kafkaProducer.send(record);

                    lastI = memory.getI();
                }
            } else {
                try {
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicConfig.getName(), gson.toJson(memory));
                    kafkaProducer.send(record);

                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
