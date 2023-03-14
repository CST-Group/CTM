package br.unicamp.ctm.representation.model;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {

    private Map<Integer, int[]> baseValues;
    private Map<Integer, int[]> signalValues;
    private Map<Integer, int[]> values;
    private Map<String, int[]> words;


    public Dictionary() {
        this.baseValues = new HashMap<>();
        this.signalValues = new HashMap<>();
        this.values = new HashMap<>();
        this.words = new HashMap<>();
    }

    public Dictionary(Map<Integer, int[]> baseValues, Map<Integer, int[]> signalValues, Map<Integer, int[]> values, Map<String, int[]> words) {
        this.baseValues = baseValues;
        this.signalValues = signalValues;
        this.values = values;
        this.words = words;
    }

    public Map<Integer, int[]> getSignalValues() {
        return signalValues;
    }

    public void setSignalValues(Map<Integer, int[]> signalValues) {
        this.signalValues = signalValues;
    }

    public Map<Integer, int[]> getValues() {
        return values;
    }

    public void setValues(Map<Integer, int[]> values) {
        this.values = values;
    }

    public Map<String, int[]> getWords() {
        return words;
    }

    public void setWords(Map<String, int[]> words) {
        this.words = words;
    }

    public Map<Integer, int[]> getBaseValues() {
        return baseValues;
    }

    public void setBaseValues(Map<Integer, int[]> baseValues) {
        this.baseValues = baseValues;
    }
}
