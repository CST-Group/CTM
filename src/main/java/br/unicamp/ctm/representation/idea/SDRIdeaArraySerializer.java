package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.converter.ValueConverter;
import br.unicamp.ctm.representation.model.ArrayDictionary;
import br.unicamp.ctm.representation.model.SDRIdea;
import br.unicamp.ctm.representation.model.SDRIdeaArray;
import br.unicamp.ctm.representation.validation.ValueValidation;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import br.unicamp.ctm.representation.model.Dictionary;

public class SDRIdeaArraySerializer {
    private int totalOfIdeas;
    private int totalOfValues;
    private int defaultValue;
    private ValueConverter<Integer> valueConverter;
    private ArrayDictionary dictionary;
    private Integer index = 0;

    private Integer startWord = 1;

    private Integer endWord = 2;

    public SDRIdeaArraySerializer(int totalOfIdeas, int totalOfValues, int defaultValue) {
        this.setTotalOfIdeas(totalOfIdeas);
        this.setTotalOfValues(totalOfValues);
        this.setDefaultValue(defaultValue);
        this.valueConverter = new ValueConverter<>();
        this.dictionary = new ArrayDictionary(new HashMap<>());
    }

    public SDRIdeaArraySerializer(int totalOfIdeas, int totalOfValues, int defaultValue, ArrayDictionary dictionary) {
        this.setTotalOfIdeas(totalOfIdeas);
        this.setTotalOfValues(totalOfValues);
        this.setDefaultValue(defaultValue);
        this.valueConverter = new ValueConverter<>();
        this.dictionary = dictionary;
    }


    public SDRIdeaArray serialize(Idea idea) throws Exception {

        this.index = 0;

        if (idea != null) {
            SDRIdeaArray sdrIdeaArray = new SDRIdeaArrayBuilder().build(this.totalOfIdeas, this.totalOfValues, this.getDefaultValue(), this.startWord);

            this.index++;

            setIdValue(idea, sdrIdeaArray.getSdr());
            setNameValue(idea, sdrIdeaArray.getSdr());
            setTypeValue(idea, sdrIdeaArray.getSdr());
            setMetadataValue(idea, sdrIdeaArray.getSdr());
            valueAnalyse(idea, sdrIdeaArray.getSdr());

            generateSDR(sdrIdeaArray, idea);

            sdrIdeaArray.getSdr()[this.index] = endWord;
//            sdrIdeaArray.getSdr()[sdrIdeaArray.getSdr().length - 1] = endWord;

            return sdrIdeaArray;

        } else {
            throw new Exception("Idea Graph is null.");
        }
    }

    private void setParentValue(Idea idea, int[] sdr) {
        setValue(sdr, (int) idea.getId());
    }

    private void setIdValue(Idea idea, int[] sdr) {
        setValue(sdr, (int) idea.getId());
    }

    private void valueAnalyse(Idea idea, int[] sdr) {
        if (ValueValidation.isArray(idea.getValue())) {
            List values = idea.getValue() instanceof List ? (List) idea.getValue()
                    : valueConverter.convertToList(idea.getValue());
            for (int i = 0; i < values.size(); i++) {
                if(ValueValidation.isPrimitive(values.get(i)))
                    setValue(sdr, (Number) values.get(i));
                else
                    setWord(sdr, getValueFromDictionary((String) values.get(i)));
            }
        } else {
            if (ValueValidation.isPrimitive(idea.getValue())) {
                if (idea.getValue().getClass().equals(Boolean.class)) {
                    setWord(sdr, getValueFromDictionary(String.valueOf(idea.getValue())));
                } else {
                    setValue(sdr, (Number) idea.getValue());
                }
            } else if (ValueValidation.isString(idea.getValue())) {
                if (idea.getValue() != null) {
                    setWord(sdr, getValueFromDictionary((String) idea.getValue()));
                }
            }
        }
    }

    public void generateSDR(SDRIdeaArray sdrIdeaArray, Idea idea) {

        for (Idea childIdea : idea.getL()) {

            setParentValue(idea, sdrIdeaArray.getSdr());
            setIdValue(childIdea, sdrIdeaArray.getSdr());
            setNameValue(childIdea, sdrIdeaArray.getSdr());
            setTypeValue(childIdea, sdrIdeaArray.getSdr());
            setMetadataValue(childIdea, sdrIdeaArray.getSdr());
            valueAnalyse(childIdea, sdrIdeaArray.getSdr());

            generateSDR(sdrIdeaArray, childIdea);
        }
    }

    private void setMetadataValue(Idea idea, int[] sdr) {
        if (idea.getValue() != null) {

            Integer metadataValue = 0;
            if (idea.getValue() instanceof List) {
                Class listClassAsArray = getListClassAsArray(
                        ((List) idea.getValue()).get(0).getClass());
                metadataValue = IdeaMetadataValues.getMetadataMap()
                        .get(listClassAsArray);
            } else {
                metadataValue = IdeaMetadataValues.getMetadataMap()
                        .get(idea.getValue().getClass());
            }

            setWord(sdr, getValueFromDictionary(String.valueOf(metadataValue)));

            int length = 0;
            if (ValueValidation.isArray(idea.getValue())) {
                List values = idea.getValue() instanceof List ? (List) idea.getValue()
                        : valueConverter.convertToList(idea.getValue());
                length = values.size();
            }

            setValue(sdr, length);
        }
    }

    private void setNameValue(Idea idea, int[] sdr) {
        if (idea.getName() != null) {
            setWord(sdr, getValueFromDictionary(idea.getName()));
        }
    }

    private void setTypeValue(Idea idea, int[] sdr) {
        setWord(sdr, getValueFromDictionary(String.valueOf(idea.getType())));
    }

    private void setWord(int[] sdr, Integer value) {
        sdr[this.index] = value;
        this.index++;
    }

    private void setValue(int[] sdr, Number value) {
        setNumericValue(sdr, value);
    }

    private Integer setNumericValue(int[] sdr, Number value) {

        List<Double> baseTenValue = valueConverter.convertNumberToBaseTen(
                Math.abs(value.doubleValue()));

        String valueString = String.format("%.2f", baseTenValue.get(0));
        valueString = valueString.replace(".", "");
        valueString = valueString.replace("-", "");

        for (int i = 0; i < Math.min(valueString.length(), 3); i++) {
            int valueInt = Integer.parseInt(String.valueOf(valueString.charAt(i)));
            sdr[index] = getValueFromDictionary(valueInt);
            index++;
        }

        int signal = getValueFromDictionary(value.intValue()>=0? "+": "-");
        sdr[index] = signal;
        index++;

        int base = getValueFromDictionary(Math.abs(baseTenValue.get(1).intValue()));
        sdr[index] = base;
        index++;

        int baseSignal = getValueFromDictionary(baseTenValue.get(1).intValue()>=0? "+": "-");
        sdr[index] = baseSignal;
        index++;

        return index;
    }

    public int getValueFromDictionary(Object value) {

        if(value instanceof Number) {
            value = ((Number) value).intValue();
        }

        if (getDictionary().getWords().containsValue(value)) {
            Object finalValue = value;
            return getDictionary().getWords().entrySet().stream()
                    .filter(entry-> entry.getValue().equals(finalValue)).findFirst().get().getKey();
        } else {
            getDictionary().getWords().put(getDictionary().getWords().size(), value);
            return getDictionary().getWords().size()-1;
        }
    }

    private Class getListClassAsArray(Class clazz) {
        if (clazz.equals(Double.class)) {
            return Double[].class;
        } else if (clazz.equals(Integer.class)) {
            return Integer[].class;
        } else if (clazz.equals(Short.class)) {
            return Short[].class;
        } else if (clazz.equals(Long.class)) {
            return Long[].class;
        } else if (clazz.equals(Byte.class)) {
            return Byte[].class;
        } else if (clazz.equals(Boolean.class)) {
            return Boolean[].class;
        } else {
            return String[].class;
        }
    }


    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ArrayDictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(ArrayDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public int getTotalOfIdeas() {
        return totalOfIdeas;
    }

    public void setTotalOfIdeas(int totalOfIdeas) {
        this.totalOfIdeas = totalOfIdeas;
    }

    public int getTotalOfValues() {
        return totalOfValues;
    }

    public void setTotalOfValues(int totalOfValues) {
        this.totalOfValues = totalOfValues;
    }
}
