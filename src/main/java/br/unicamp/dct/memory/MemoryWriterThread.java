package br.unicamp.dct.memory;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class MemoryWriterThread extends Thread {

    private final Memory memory;
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final Gson gson;
    private final TopicConfig topicConfig;

    private final Logger logger = LoggerFactory.getLogger(MemoryWriterThread.class);

    public MemoryWriterThread(Memory memory, KafkaConsumer<String, String> kafkaConsumer,
                              TopicConfig topicConfig) {
        this.memory = memory;
        this.kafkaConsumer = kafkaConsumer;
        this.gson = new Gson();
        this.topicConfig = topicConfig;
    }

    @Override
    public void run() {
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1));

            for (ConsumerRecord<String, String> record : records) {
                try {
                  Memory recordMemory = gson.fromJson(record.value(), MemoryObject.class);
                  memory.setI(recordMemory.getI());
                  memory.setEvaluation(recordMemory.getEvaluation());
                } catch (JsonSyntaxException jsonSyntaxException) {
                    logger.error(String.format("Could not convert message from topic:%s - Message: %s", topicConfig.getName(), record.value()));
                }
            }
        }
    }
}
