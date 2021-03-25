package br.unicamp.dct.kafka;

interface MessageConverter<T> {
    public T convert(String message);

    public T convert(byte[] message);
}
