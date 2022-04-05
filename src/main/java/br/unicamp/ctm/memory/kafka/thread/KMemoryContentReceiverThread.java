package br.unicamp.ctm.memory.kafka.thread;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.ctm.memory.kafka.config.TopicConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import org.apache.log4j.Logger;

public class KMemoryContentReceiverThread extends Thread {

  private final Memory memory;
  private final KafkaConsumer<String, String> kafkaConsumer;
  private final Gson gson;
  private final TopicConfig topicConfig;

  private final Logger logger = Logger.getLogger(KMemoryContentReceiverThread.class);

  public KMemoryContentReceiverThread(Memory memory, KafkaConsumer<String, String> kafkaConsumer,
      TopicConfig topicConfig) {
    this.memory = memory;
    this.kafkaConsumer = kafkaConsumer;
    this.gson = new Gson();
    this.topicConfig = topicConfig;
  }

  @Override
  public void run() {
    logger.info(
        String.format("Content receiver thread initialized for memory %s.", memory.getName()));

    while (true) {

      ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(10));

      for (ConsumerRecord<String, String> record : records) {
        try {
          Memory recordMemory = gson.fromJson(record.value(), MemoryObject.class);

          if (topicConfig.getClassName() != null) {
            if (!topicConfig.getClassName().trim().equals("")) {
              if (recordMemory.getI() != null) {
                memory.setI(gson.fromJson(String.valueOf(recordMemory.getI()),
                    Class.forName(topicConfig.getClassName())));
              } else {
                memory.setI(null);
              }
            }
          } else {
            memory.setI(recordMemory.getI());
          }

          memory.setEvaluation(recordMemory.getEvaluation());
        } catch (JsonSyntaxException jsonSyntaxException) {
          logger.error(String.format("Could not convert message from topic:%s - Message: %s",
              topicConfig.getName(), record.value()));
        } catch (ClassNotFoundException e) {
          logger.error(String.format("Could not convert message from topic:%s - To Class: %s",
              topicConfig.getName(), topicConfig.getClassName()));
        }
      }
    }
  }
}
