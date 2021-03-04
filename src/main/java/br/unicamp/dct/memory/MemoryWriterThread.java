package br.unicamp.dct.memory;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;

public class MemoryWriterThread extends Thread {

    private Memory memory;
    private KafkaConsumer<String, String> kafkaConsumer;
    private Gson gson;

    public MemoryWriterThread(Memory memory, KafkaConsumer<String, String> kafkaConsumer) {
        this.memory = memory;
        this.kafkaConsumer = kafkaConsumer;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(10));

            for (ConsumerRecord<String, String> record : records) {
                Memory recordMemory = gson.fromJson(record.value(), MemoryObject.class);
                memory.setI(recordMemory.getI());
                memory.setEvaluation(recordMemory.getEvaluation());
            }
        }
    }
}
