package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.converter.ValueConverter;
import br.unicamp.ctm.representation.model.SDRIdea;
import br.unicamp.ctm.representation.validation.ValueValidation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SDRIdeaSerializer {

  private int channels;
  private int rows;
  private int columns;
  private int defaultValue;
  private int activeValue;
  private ValueConverter<Integer> valueConverter;
  private Map<String, int[]> dictionary;
  private Map<Integer, int[]> values;
  private int channelCounter = 1;

  public SDRIdeaSerializer(int channels, int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
    this.channels = channels;
    this.setDefaultValue(0);
    this.setActiveValue(1);
    this.valueConverter = new ValueConverter<>();
    this.setDictionary(new HashMap<>());
    this.setValues(new HashMap<>());
  }

  public SDRIdeaSerializer(int channels, int rows, int columns, int defaultValue, int activeValue) {
    this.rows = rows;
    this.columns = columns;
    this.channels = channels;
    this.setDefaultValue(defaultValue);
    this.setActiveValue(activeValue);
    this.valueConverter = new ValueConverter<>();
    this.setDictionary(new HashMap<>());
    this.setValues(new HashMap<>());
  }

  public SDRIdeaSerializer(int channels, int rows, int columns, int defaultValue, int activeValue,
      Map<String, int[]> dictionary, Map<Integer, int[]> values) {
    this.rows = rows;
    this.columns = columns;
    this.channels = channels;
    this.setDefaultValue(defaultValue);
    this.setActiveValue(activeValue);
    this.valueConverter = new ValueConverter<>();
    this.setDictionary(dictionary);
    this.setValues(values);
  }


  public SDRIdea serialize(Idea idea) throws Exception {

    if (idea != null) {
      SDRIdea sdrIdea = new SDRIdeaBuilder().build(channels, rows, columns, getDefaultValue(),
          getActiveValue());

      setIdValue(idea, sdrIdea.getSdr(), 0);
      setNameValue(idea, sdrIdea.getSdr(), 0);
      setTypeValue(idea, sdrIdea.getSdr(), 0);
      setMetadataValue(idea, sdrIdea.getSdr(), 0);
      valueAnalyse(idea, sdrIdea.getSdr(), 0);

      generateSDR(sdrIdea, idea);

      channelCounter = 1;

      return sdrIdea;

    } else {
      throw new Exception("Idea Graph is null.");
    }
  }

  private void setParentValue(Idea idea, int[][][] sdr, int channel) {
    setNumericValue(sdr, channel, 0, columns, (int) idea.getId());
  }

  private void setIdValue(Idea idea, int[][][] sdr, int channel) {
    setNumericValue(sdr, channel, 2, columns, (int) idea.getId());
  }

  private void valueAnalyse(Idea idea, int[][][] sdr, int channel) {
    if (ValueValidation.isArray(idea.getValue())) {
      List values = idea.getValue() instanceof List ? (List) idea.getValue()
          : valueConverter.convertToList(idea.getValue());
      for (int i = 0; i < values.size(); i++) {
        setNumericValue(sdr, channel, 11 + i * 2, columns, (Number) values.get(i));
      }
    } else {
      if (ValueValidation.isPrimitive(idea.getValue())) {
        if (idea.getValue().getClass().equals(Boolean.class)) {
          setValue(sdr, channel, 11, getArrayFromDictionary(String.valueOf(idea.getValue())));
        } else {
          setNumericValue(sdr, channel, 11, columns, (Number) idea.getValue());
        }
      } else if (ValueValidation.isString(idea.getValue())) {
        if (idea.getValue() != null) {
          setValue(sdr, channel, 11, getArrayFromDictionary((String) idea.getValue()));
        }
      }
    }
  }

  public void generateSDR(SDRIdea sdrIdea, Idea idea) {

    for (Idea childIdea : idea.getL()) {

      setParentValue(idea, sdrIdea.getSdr(), channelCounter);
      setIdValue(childIdea, sdrIdea.getSdr(), channelCounter);
      setNameValue(childIdea, sdrIdea.getSdr(), channelCounter);
      setTypeValue(childIdea, sdrIdea.getSdr(), channelCounter);
      setMetadataValue(childIdea, sdrIdea.getSdr(), channelCounter);
      valueAnalyse(childIdea, sdrIdea.getSdr(), channelCounter);

      channelCounter++;
      generateSDR(sdrIdea, childIdea);
    }
  }

  private void setMetadataValue(Idea idea, int[][][] sdr, int channel) {
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

      setNumericValue(sdr, channel, 7, columns, metadataValue);

      int length = 0;

      if (ValueValidation.isArray(idea.getValue())) {
        List values = idea.getValue() instanceof List ? (List) idea.getValue()
            : valueConverter.convertToList(idea.getValue());
        length = values.size();
      }

      setNumericValue(sdr, channel, 9, columns, length);
    }
  }

  private void setNameValue(Idea idea, int[][][] sdr, int channel) {
    if (idea.getName() != null && getArrayFromDictionary(idea.getName()) != null) {
      setValue(sdr, channel, 4, getArrayFromDictionary(idea.getName()));
    }
  }

  private void setTypeValue(Idea idea, int[][][] sdr, int channel) {
    setNumericValue(sdr, channel, 5, columns, (int) idea.getType());
  }

  private void setValue(int[][][] sdr, int channel, int row, int[] value) {
    sdr[channel][row] = value;
  }

  private void setNumericValue(int[][][] sdr, int channel, int row, int length, Number value) {
    int range = length / 4;

    List<Double> baseTenValue = valueConverter.convertNumberToBaseTen(
        Math.abs(value.doubleValue()));

    String valueString = String.valueOf(baseTenValue.get(0));
    valueString = valueString.replace(".", "");
    valueString = valueString.replace("-", "");

    for (int i = 0; i < Math.min(valueString.length(), 4); i++) {
      int valueInt = Integer.parseInt(String.valueOf(valueString.charAt(i)));
      int[] valueSDR = getArrayFromValues(valueInt, range);

      for (int j = 0; j < valueSDR.length; j++) {
        sdr[channel][row][i * range + j] = valueSDR[j];
      }
    }

    int base = baseTenValue.get(1).intValue();
    int[] baseSDR = getArrayFromValues(Math.abs(base), range);

    for (int i = 0; i < baseSDR.length; i++) {
      sdr[channel][row + 1][i] = baseSDR[i];
    }

    if (value.doubleValue() < 0) {
      sdr[channel][row + 1][baseSDR.length] = 1;
    } else {
      sdr[channel][row + 1][baseSDR.length] = 0;
    }

    if (base < 0) {
      sdr[channel][row + 1][baseSDR.length + 1] = 1;
    } else {
      sdr[channel][row + 1][baseSDR.length + 1] = 0;
    }

  }

  public int[] getArrayFromDictionary(String word) {
    if (getDictionary().containsKey(word)) {
      return getDictionary().get(word);
    } else {
      int[] value = generateContent(columns, false, getDictionary(), new HashMap<>());
      getDictionary().put(word, value);
      return value;
    }
  }


  public int[] getArrayFromValues(Integer value, Integer length) {
    if (getValues().containsKey(value)) {
      return getValues().get(value);
    } else {
      int[] arrayValue = generateContent(length, true, new HashMap<>(), getValues());
      getValues().put(value, arrayValue);
      return arrayValue;
    }
  }

  private int[] generateContent(int length, boolean isValue, Map<String, int[]> dictionary,
      Map<Integer, int[]> values) {

    boolean retry = true;

    while (retry) {
      int[] value = generateValue(length);

      retry = isValue ? values.entrySet().stream()
          .filter(entry -> entry.getValue().length == length)
          .filter(entry -> compareValue(entry.getValue(), value))
          .collect(Collectors.toList()).size() > 0 :

          dictionary.entrySet().stream()
              .filter(entry -> entry.getValue().length == length)
              .filter(entry -> compareValue(entry.getValue(), value))
              .collect(Collectors.toList()).size() > 0;

      if (!retry) {
        return value;
      }
    }

    return initializeValue(new int[length], getDefaultValue());
  }


  private int[] generateValue(int length) {

    int w = length / 2;
    Random random = new Random();

    int[] value = new int[length];

    for (int i = 0; i < w; i++) {
      boolean retry = true;

      while (retry) {
        int index = random.nextInt(length);
        if (value[index] != 1) {
          value[index] = getActiveValue();
          retry = false;
        }
      }
    }

    return value;
  }


  private int[] initializeValue(int[] value, int defaultValue) {
    for (int i = 0; i < value.length; i++) {
      value[i] = defaultValue;
    }
    return value;
  }

  private boolean compareValue(int[] newValue, int[] value) {

    if (newValue.length == value.length) {

      for (int i = 0; i < newValue.length; i++) {
        if (newValue[i] != value[i]) {
          return false;
        }
      }

      return true;
    }

    return false;
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

  public int getActiveValue() {
    return activeValue;
  }

  public void setActiveValue(int activeValue) {
    this.activeValue = activeValue;
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
}
