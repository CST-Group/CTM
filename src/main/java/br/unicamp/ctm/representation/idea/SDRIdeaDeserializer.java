package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.model.SDRIdea;
import br.unicamp.ctm.representation.validation.ValueValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class SDRIdeaDeserializer {

    private boolean toRaw;
    private ValueValidation valueValidation;
    private Map<String, int[]> dictionary;
    private Map<Integer, int[]> values;

    public SDRIdeaDeserializer(Map<String, int[]> dictionary, Map<Integer, int[]> values) {
        this.valueValidation = new ValueValidation();
        this.setDictionary(dictionary);
        this.setValues(values);
        this.toRaw = false;
    }

    public SDRIdeaDeserializer(Map<String, int[]> dictionary, Map<Integer, int[]> values, boolean toRaw) {
        this.valueValidation = new ValueValidation();
        this.setDictionary(dictionary);
        this.setValues(values);
        this.toRaw = toRaw;
    }

    public Idea deserialize(SDRIdea sdrIdea) {

        List<Idea> ideaList = new ArrayList<>();

        generateIdeaGraph(sdrIdea, ideaList);

        return ideaList.size() > 0 ? ideaList.stream().findFirst().get() : null;
    }

    private void generateIdeaGraph(SDRIdea sdrIdea, List<Idea> ideaList) {

        int[][][] sdr = sdrIdea.getSdr();

        Map<Integer, Integer> ideaRelationship = new HashMap<>();

        for (int i = 0; i < sdr.length; i++) {

            int[][] sdrChannel = sdr[i];

            if (isNullableSDR(sdrChannel))
                continue;

            Integer parentId = null;
            if (i != 0)
                parentId = (int) extractValue(sdrChannel, 0, Integer.class);

            int id = (int) extractValue(sdrChannel, 2, Integer.class);
            String name = extractWord(sdrChannel, 4);
            int type = (int) extractValue(sdrChannel, 5, Integer.class);
            int metadata = (int) extractValue(sdrChannel, 7, Integer.class);
            int length = (int) extractValue(sdrChannel, 9, Integer.class);

            Idea idea = new Idea(name, null, type);
            idea.setId(id);
            setValue(idea, sdrChannel, metadata, length);
            idea.setType(type);

            if (parentId != null)
                ideaRelationship.put(id, parentId);

            ideaList.add(idea);
        }

        for (int i = 0; i < ideaList.size(); i++) {
            Idea idea = ideaList.get(i);

            List<Entry<Integer, Integer>> relations = ideaRelationship.entrySet().stream()
                    .filter(entry -> entry.getValue() == idea.getId()).collect(Collectors.toList());

            for (Entry<Integer, Integer> relation : relations) {
                ideaList.stream().filter(ideaFilter -> ideaFilter.getId() == relation.getKey()).findFirst()
                        .ifPresent(childIdea -> idea.getL().add(childIdea));
            }
        }

    }

    private void setValue(Idea idea, int[][] sdrChannel, int metadata, int length) {

        Optional<Entry<Class, Integer>> entryOptional = IdeaMetadataValues.getMetadataMap()
                .entrySet().stream().filter(entry -> entry.getValue() == metadata)
                .findFirst();

        if (entryOptional.isPresent()) {
            Class clazz = entryOptional.get().getKey();
            if (ValueValidation.isArray(clazz)) {
                setArrayValue(idea, sdrChannel, length, clazz);
            } else if (ValueValidation.isPrimitive(clazz)) {
                setPrimitiveValue(idea, sdrChannel, 11, clazz);
            } else if (ValueValidation.isString(clazz)) {
                idea.setValue(extractWord(sdrChannel, 11));
            }
        }
    }

    private void setArrayValue(Idea idea, int[][] sdrChannel, int length, Class clazz) {

        if (clazz.getCanonicalName().equals(double[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Double[].class.getCanonicalName())) {
            idea.setValue(new double[length]);
        } else if (clazz.getCanonicalName().equals(int[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Integer[].class.getCanonicalName())) {
            idea.setValue(new int[length]);
        } else if (clazz.getCanonicalName().equals(float[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Float[].class.getCanonicalName())) {
            idea.setValue(new float[length]);
        } else if (clazz.getCanonicalName().equals(short[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Short[].class.getCanonicalName())) {
            idea.setValue(new short[length]);
        } else if (clazz.getCanonicalName().equals(boolean[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Boolean[].class.getCanonicalName())) {
            idea.setValue(new boolean[length]);
        } else if (clazz.getCanonicalName().equals(long[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Long[].class.getCanonicalName())) {
            idea.setValue(new long[length]);
        } else if (clazz.getCanonicalName().equals(long[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Long[].class.getCanonicalName())) {
            idea.setValue(new String[length]);
        }

        for (int i = 0; i < length; i++) {
            if (clazz.getCanonicalName().equals(double[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Double[].class.getCanonicalName())) {
                ((double[]) idea.getValue())[i] = (double) extractValue(sdrChannel, 11 + i * 2, Double.class);
            } else if (clazz.getCanonicalName().equals(int[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Integer[].class.getCanonicalName())) {
                ((int[]) idea.getValue())[i] = (int) extractValue(sdrChannel, 11 + i * 2, Integer.class);
            } else if (clazz.getCanonicalName().equals(float[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Float[].class.getCanonicalName())) {
                ((float[]) idea.getValue())[i] = (float) extractValue(sdrChannel, 11 + i * 2, Float.class);
            } else if (clazz.getCanonicalName().equals(short[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Short[].class.getCanonicalName())) {
                ((short[]) idea.getValue())[i] = (short) extractValue(sdrChannel, 11 + i * 2, Short.class);
            } else if (clazz.getCanonicalName().equals(boolean[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Boolean[].class.getCanonicalName())) {
                ((boolean[]) idea.getValue())[i] = Boolean.parseBoolean(extractWord(sdrChannel, 11 + i * 2));
            } else if (clazz.getCanonicalName().equals(long[].class.getCanonicalName()) || clazz.getCanonicalName().equals(Long[].class.getCanonicalName())) {
                ((long[]) idea.getValue())[i] = (long) extractValue(sdrChannel, 11 + i * 2, Long.class);
            } else if (clazz.getCanonicalName().equals(String[].class.getCanonicalName()) || clazz.getCanonicalName().equals(String[].class.getCanonicalName())) {
                ((String[]) idea.getValue())[i] = extractWord(sdrChannel, 11 + i);
            }
        }

    }

    private void setPrimitiveValue(Idea idea, int[][] sdrChannel, int row, Class clazz) {
        if (clazz.getCanonicalName().equals(int.class.getCanonicalName())) {
            idea.setValue(extractValue(sdrChannel, row, Integer.class));
        } else if (clazz.getCanonicalName().equals(long.class.getCanonicalName())) {
            idea.setValue(extractValue(sdrChannel, row, Long.class));
        } else if (clazz.getCanonicalName().equals(double.class.getCanonicalName())) {
            idea.setValue(extractValue(sdrChannel, row, Double.class));
        } else if (clazz.getCanonicalName().equals(float.class.getCanonicalName())) {
            idea.setValue(extractValue(sdrChannel, row, Float.class));
        } else if (clazz.getCanonicalName().equals(boolean.class.getCanonicalName())) {
            idea.setValue(Boolean.valueOf(extractWord(sdrChannel, 11)));
        } else if (clazz.getCanonicalName().equals(short.class.getCanonicalName())) {
            idea.setValue(extractValue(sdrChannel, row, Short.class));
        } else if (clazz.getCanonicalName().equals(byte.class.getCanonicalName())) {
            idea.setValue(extractValue(sdrChannel, row, Byte.class));
        }
    }

    private String extractWord(int[][] sdrChannel, int row) {
        Optional<Entry<String, int[]>> wordOptional = getDictionary().entrySet().stream()
                .filter(entry -> valueValidation.compareValue(entry.getValue(), sdrChannel[row])).findFirst();

        return wordOptional.isPresent() ? wordOptional.get().getKey() : "";
    }

    private Object extractValue(int[][] sdrChannel, int row, Class clazz) {
        if (toRaw) {
            return extractValueToRaw(sdrChannel, row, clazz);
        }

        return extractValueSDR(sdrChannel, row, clazz);
    }

    private Object extractValueToRaw(int[][] sdrChannel, int row, Class clazz) {

        String stringValue = "";
        for (int i = 0; i < 2; i++) {
            for (int element : sdrChannel[row + i]) {
                stringValue += element;
            }
        }

        if (!stringValue.contains("1"))
            return getValueAccordingType(0, clazz);

        Number value = Double.longBitsToDouble(Long.parseUnsignedLong(stringValue, 2));

        return getValueAccordingType(value, clazz);
    }


    private Object getValueAccordingType(Number value, Class clazz) {
        if (clazz == Integer.class)
            return value.intValue();
        else if (clazz == Float.class)
            return value.floatValue();
        else if (clazz == Short.class)
            return value.shortValue();
        else if (clazz == Byte.class)
            return value.byteValue();
        else if (clazz == Double.class)
            return value.doubleValue();
        else if (clazz == Long.class) ;
            return value.longValue();
    }

    private Object extractValueSDR(int[][] sdrChannel, int row, Class clazz) {

        int length = sdrChannel[row].length;
        int range = length / 4;

        String valueString = "";
        for (int i = 0; i < 4; i++) {

            int[] valueSDR = buildSDR(range, sdrChannel[row], i);

            Optional<Entry<Integer, int[]>> valueOptional = getValues().entrySet().stream()
                    .filter(entry -> valueValidation.compareValue(entry.getValue(), valueSDR)).findFirst();

            if (valueOptional.isPresent()) {
                valueString += valueOptional.get().getKey();
            }

            if (i == 0)
                valueString += ".";
        }

        if (valueString.length() == 1 || valueString.isEmpty())
            return 0d;

        int[] baseSDR = buildSDR(range, sdrChannel[row + 1], 0);

        Optional<Entry<Integer, int[]>> baseOptional = getValues().entrySet().stream()
                .filter(entry -> valueValidation.compareValue(entry.getValue(), baseSDR)).findFirst();

        int base = 0;

        if (baseOptional.isPresent()) {
            base = baseOptional.get().getKey();
        }

        int[] signalValueSDR = buildSDR(range, sdrChannel[row + 1], 1);

        Optional<Entry<Integer, int[]>> signalValueOptional = getValues().entrySet().stream()
                .filter(entry -> valueValidation.compareValue(entry.getValue(), signalValueSDR)).findFirst();

        int valueSignal = 0;

        if (signalValueOptional.isPresent()) {
            valueSignal = signalValueOptional.get().getKey() == 1? -1 : 1;
        }

        int[] signalBaseValueSDR = buildSDR(range, sdrChannel[row + 1], 2);

        Optional<Entry<Integer, int[]>> signalBaseValueOptional = getValues().entrySet().stream()
                .filter(entry -> valueValidation.compareValue(entry.getValue(), signalBaseValueSDR)).findFirst();

        int baseSignal = 0;

        if (signalBaseValueOptional.isPresent()) {
            baseSignal = signalBaseValueOptional.get().getKey() == 1? -1 : 1;
        }


//        int valueSignal = sdrChannel[row + 1][range] * -1 == 0 ? 1 : -1;
//        int baseSignal = sdrChannel[row + 1][range + 1] * -1 == 0 ? 1 : -1;

        Number number = Double.parseDouble(valueString) * Math.pow(10, base * baseSignal) * valueSignal;

        if (clazz == Integer.class)
            return number.intValue();
        else if (clazz == Float.class)
            return number.floatValue();
        else if (clazz == Short.class)
            return number.shortValue();
        else if (clazz == Byte.class)
            return number.byteValue();
        else if (clazz == Double.class)
            return number.doubleValue();
        else if (clazz == Long.class) ;
        return number.longValue();
    }

    private int[] buildSDR(int range, int[] sdrRow, int interval) {
        int[] sdr = new int[range];

        for (int i = 0; i < range; i++) {
            sdr[i] = sdrRow[interval * range + i];
        }

        return sdr;
    }

    public Map<String, int[]> getDictionary() {
        return dictionary;
    }

    public void setDictionary(Map<String, int[]> dictionary) {
        this.dictionary = dictionary;
    }

    public Map<Integer, int[]> getValues() {
        return values;
    }

    public void setValues(Map<Integer, int[]> values) {
        this.values = values;
    }

    public boolean isNullableSDR(int[][] sdr) {
        int sumCheck = 0;
        for (int i = 0; i < sdr.length; i++) {
            for (int j = 0; j < sdr[i].length; j++) {
                sumCheck += sdr[i][j];
            }
        }

        return sumCheck <= 10;
    }
}
