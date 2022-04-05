package br.unicamp.ctm.memory.kafka.thread;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.ctm.memory.kafka.KDistributedMemoryBehavior;
import br.unicamp.ctm.memory.kafka.config.TopicConfig;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

public class KMemoryContentPublisherThread extends Thread {

  private final Memory memory;
  private Object lastI = null;
  private Double lastEvaluate = 0d;
  private final KafkaProducer<String, String> kafkaProducer;
  private final Gson gson;
  private final TopicConfig topicConfig;

  private final Logger logger = Logger.getLogger(KMemoryContentReceiverThread.class);

  public KMemoryContentPublisherThread(Memory memory, KafkaProducer<String, String> kafkaProducer,
      TopicConfig topicConfig) {
    this.memory = memory;
    this.kafkaProducer = kafkaProducer;
    this.topicConfig = topicConfig;

    this.gson = new Gson();
  }

  @Override
  public void run() {
    logger.info(
        String.format("Content publisher thread initialized for memory %s.", memory.getName()));

    while (true) {
      try {
        if (topicConfig.getDistributedMemoryBehavior() == KDistributedMemoryBehavior.TRIGGERED) {
          synchronized (memory) {
            memory.wait();
          }

          String json = gson.toJson(memory);

          ProducerRecord<String, String> record = new ProducerRecord<String, String>(
              topicConfig.getName(), json);
          kafkaProducer.send(record);

        } else {
          if (memory.getI() != lastI || memory.getEvaluation() != lastEvaluate) {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                topicConfig.getName(), gson.toJson(memory));
            kafkaProducer.send(record);
            lastI = memory.getI();
            lastEvaluate = memory.getEvaluation();
          }

          Thread.sleep(10);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
