package br.unicamp.ctm.representation.model;

import java.util.Map;

public class ArrayDictionary {

    private Map<Integer, Object> words;

    public ArrayDictionary(Map<Integer, Object> words) {
        this.setWords(words);
    }

    public Map<Integer, Object> getWords() {
        return words;
    }

    public void setWords(Map<Integer, Object> words) {
        this.words = words;
    }
}
