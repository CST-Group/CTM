package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.converter.ValueConverter;
import br.unicamp.ctm.representation.model.ArrayDictionary;
import br.unicamp.ctm.representation.model.SDRIdeaArray;
import br.unicamp.ctm.representation.validation.ValueValidation;

import java.util.*;
import java.util.stream.Collectors;

public class SDRIdeaArrayDeserializer {
    private ArrayDictionary dictionary;
    private ValueConverter<Integer> valueConverter;
    private Integer index = 0;

    private Integer startWord = 1;
    private Integer endWord = 2;

    public SDRIdeaArrayDeserializer(ArrayDictionary dictionary) {
        this.dictionary = dictionary;
        this.valueConverter = new ValueConverter<>();
    }

    public Idea deserialize(SDRIdeaArray sdrIdeaArray) throws Exception {
        if (sdrIdeaArray == null || sdrIdeaArray.getSdr() == null) {
            throw new Exception("SDR Idea Array is null or empty.");
        }

        Map<Long, Long> ideaRelationship = new HashMap<>();
        List<Idea> ideaList = new ArrayList<>();

        int[] sdr = sdrIdeaArray.getSdr();

        index = 0;

        while(index < sdr.length) {

            if (sdr[index] == startWord) {
                index++;
                continue;
            } else if (sdr[index] == endWord) {
                break;
            }

            Idea idea = new Idea();

            Long parentId = null;
            if(ideaList.size() > 0)
                parentId = (Long) getValueAccordingType(getNumericValue(sdr), Long.class);

            idea.setId((long) getValueAccordingType(getNumericValue(sdr), Long.class));
            idea.setName(getStringValue(sdr));
            idea.setType(Integer.parseInt(getStringValue(sdr)));
            idea.setValue(getValue(sdr));

            if (parentId != null)
                ideaRelationship.put(idea.getId(), parentId);

            ideaList.add(idea);
        }

        for (int i = 0; i < ideaList.size(); i++) {
            Idea ideaElement = ideaList.get(i);

            List<Map.Entry<Long, Long>> relations = ideaRelationship.entrySet().stream()
                    .filter(entry -> entry.getValue() == ideaElement.getId()).collect(Collectors.toList());

            for (Map.Entry<Long, Long> relation : relations) {
                ideaList.stream().filter(ideaFilter -> ideaFilter.getId() == relation.getKey()).findFirst()
                        .ifPresent(childIdea -> ideaElement.getL().add(childIdea));
            }
        }

        return ideaList.size() > 0 ? ideaList.stream().findFirst().get() : null;
    }



    private Number getNumericValue(int[] sdr) {

        StringBuilder valueString = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            valueString.append(dictionary.getWords().get(sdr[index]));
            index++;
        }

        valueString = new StringBuilder(valueString.charAt(0) + "." + valueString.charAt(1) + valueString.charAt(2));

        String signal = (String) dictionary.getWords().get(sdr[index++]);
        Integer base = (Integer) dictionary.getWords().get(sdr[index++]);
        String baseSignal = (String) dictionary.getWords().get(sdr[index++]);


        double value = Double.parseDouble(valueString.toString()) * Math.pow(10, base * (baseSignal.equals("+") ? 1 : -1));
        value = signal.equals("+") ? value : -value;
        value = Math.round(value * 100.0) / 100.0;

        return value;
    }

    private String getStringValue(int[] sdr) {
        String value = (String) dictionary.getWords().get(sdr[index]);
        index++;
        return value;
    }

    private Object getValue(int[] sdr) {

        Integer metadataValue = Integer.parseInt(dictionary.getWords().get(sdr[index]).toString());
        index++;

        int length = (Integer) getValueAccordingType(getNumericValue(sdr), Integer.class);

        Optional<Map.Entry<Class, Integer>> entryOptional = IdeaMetadataValues.getMetadataMap()
                .entrySet().stream().filter(entry -> entry.getValue() == metadataValue)
                .findFirst();

        if (entryOptional.isPresent()) {
            Class clazz = entryOptional.get().getKey();
            if (ValueValidation.isArray(clazz)) {
                return getArrayValue(sdr, length, clazz);
            } else if (ValueValidation.isPrimitive(clazz)) {
                return getValueAccordingType(getNumericValue(sdr), clazz);
            } else if (ValueValidation.isString(clazz)) {
                return getStringValue(sdr);
            }
        }

        return null;
    }

    private Object getArrayValue(int[] sdr, int length, Class clazz) {

        Object array = null;

        if (clazz.getCanonicalName().equals(double[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Double[].class.getCanonicalName())) {
            array = new double[length];
        } else if (clazz.getCanonicalName().equals(int[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Integer[].class.getCanonicalName())) {
            array = new int[length];
        } else if (clazz.getCanonicalName().equals(float[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Float[].class.getCanonicalName())) {
            array = new float[length];
        } else if (clazz.getCanonicalName().equals(short[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Short[].class.getCanonicalName())) {
            array = new short[length];
        } else if (clazz.getCanonicalName().equals(boolean[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Boolean[].class.getCanonicalName())) {
            array = new boolean[length];
        } else if (clazz.getCanonicalName().equals(long[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Long[].class.getCanonicalName())) {
            array = new long[length];
        } else if (clazz.getCanonicalName().equals(long[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Long[].class.getCanonicalName())) {
            array = new String[length];
        }

        for (int i = 0; i < length; i++) {
            if (clazz.getCanonicalName().equals(double[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Double[].class.getCanonicalName())) {
                ((double[]) array)[i] = (double) getValueAccordingType(getNumericValue(sdr), Double.class);
            } else if (clazz.getCanonicalName().equals(int[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Integer[].class.getCanonicalName())) {
                ((int[]) array)[i] = (int) getValueAccordingType(getNumericValue(sdr), Integer.class);
            } else if (clazz.getCanonicalName().equals(float[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Float[].class.getCanonicalName())) {
                ((float[]) array)[i] = (float) getValueAccordingType(getNumericValue(sdr), Float.class);;
            } else if (clazz.getCanonicalName().equals(short[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Short[].class.getCanonicalName())) {
                ((short[]) array)[i] = (short) getValueAccordingType(getNumericValue(sdr), Short.class);;
            } else if (clazz.getCanonicalName().equals(boolean[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Boolean[].class.getCanonicalName())) {
                ((boolean[]) array)[i] = Boolean.parseBoolean(getStringValue(sdr));
            } else if (clazz.getCanonicalName().equals(long[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Long[].class.getCanonicalName())) {
                ((long[]) array)[i] = (long) getValueAccordingType(getNumericValue(sdr), Long.class);;
            } else if (clazz.getCanonicalName().equals(String[].class.getCanonicalName()) || clazz.getCanonicalName().equals(String[].class.getCanonicalName())) {
                ((String[]) array)[i] = getStringValue(sdr);
            }
        }

        return array;

    }

    private Object getValueAccordingType(Number value, Class clazz) {
        if (clazz == Integer.class || clazz == int.class)
            return value.intValue();
        else if (clazz == Float.class || clazz == float.class)
            return value.floatValue();
        else if (clazz == Short.class || clazz == short.class)
            return value.shortValue();
        else if (clazz == Byte.class || clazz == byte.class)
            return value.byteValue();
        else if (clazz == Double.class || clazz == double.class)
            return value.doubleValue();
        else if (clazz == Long.class || clazz == long.class)
            return value.longValue();
        else
            return value.longValue();
    }

}