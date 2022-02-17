package br.unicamp.ctm.memory.kafka.exception;

public class TopicNotFoundException extends Exception {
    public TopicNotFoundException(String message) {
        super(message);
    }
}
