package br.unicamp.dct.thread;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.dct.memory.DistributedMemoryBehavior;
import br.unicamp.dct.kafka.config.TopicConfig;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class MemoryContentReaderThread extends Thread {

    private final Memory memory;
    private Object lastI;
    private final KafkaProducer<String, String> kafkaProducer;
    private final Gson gson;
    private final TopicConfig topicConfig;

    public MemoryContentReaderThread(Memory memory, KafkaProducer<String, String> kafkaProducer, TopicConfig topicConfig) {
        this.memory = memory;
        this.kafkaProducer = kafkaProducer;
        this.topicConfig = topicConfig;

        this.gson = new Gson();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if(topicConfig.getDistributedMemoryBehavior() == DistributedMemoryBehavior.TRIGGERED) {
                    if(memory.getI() != lastI) {
                        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicConfig.getName(), gson.toJson(memory));
                        kafkaProducer.send(record);

                        lastI = memory.getI();
                        this.wait();
                    }
                } else {
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicConfig.getName(), gson.toJson(memory));
                    kafkaProducer.send(record);
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
