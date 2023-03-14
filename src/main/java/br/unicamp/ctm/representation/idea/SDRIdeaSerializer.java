package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.converter.ValueConverter;
import br.unicamp.ctm.representation.model.SDRIdea;
import br.unicamp.ctm.representation.validation.ValueValidation;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import br.unicamp.ctm.representation.model.Dictionary;

public class SDRIdeaSerializer {

  private boolean toRaw;
  private int channels;
  private int rows;
  private int columns;
  private int defaultValue;
  private int activeValue;
  private ValueConverter<Integer> valueConverter;
  private Dictionary dictionary;

  private int channelCounter = 1;

  public SDRIdeaSerializer(int channels, int rows, int columns, boolean toRaw) {
    this.rows = rows;
    this.columns = columns;
    this.channels = channels;
    this.setDefaultValue(0);
    this.setActiveValue(1);
    this.valueConverter = new ValueConverter<>();
    this.dictionary = new Dictionary(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    this.toRaw = toRaw;
  }

  public SDRIdeaSerializer(int channels, int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
    this.channels = channels;
    this.setDefaultValue(0);
    this.setActiveValue(1);
    this.valueConverter = new ValueConverter<>();
    this.dictionary = new Dictionary(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    this.toRaw = false;
  }

  public SDRIdeaSerializer(int channels, int rows, int columns, int defaultValue, int activeValue) {
    this.rows = rows;
    this.columns = columns;
    this.channels = channels;
    this.setDefaultValue(defaultValue);
    this.setActiveValue(activeValue);
    this.valueConverter = new ValueConverter<>();
    this.dictionary = new Dictionary(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    this.toRaw = false;
  }

  public SDRIdeaSerializer(int channels, int rows, int columns, int defaultValue, int activeValue, Dictionary dictionary) {
    this.rows = rows;
    this.columns = columns;
    this.channels = channels;
    this.setDefaultValue(defaultValue);
    this.setActiveValue(activeValue);
    this.valueConverter = new ValueConverter<>();
    this.dictionary = dictionary;
    this.toRaw = false;
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
    setValue(sdr, channel, 0, columns, (int) idea.getId());
  }

  private void setIdValue(Idea idea, int[][][] sdr, int channel) {
    setValue(sdr, channel, 2, columns, (int) idea.getId());
  }

  private void valueAnalyse(Idea idea, int[][][] sdr, int channel) {
    if (ValueValidation.isArray(idea.getValue())) {
      List values = idea.getValue() instanceof List ? (List) idea.getValue()
          : valueConverter.convertToList(idea.getValue());
      for (int i = 0; i < values.size(); i++) {
        if(ValueValidation.isPrimitive(values.get(i)))
          setValue(sdr, channel, 11 + i * 2, columns, (Number) values.get(i));
        else
          setWord(sdr, channel, 11 + i, getArrayFromWords((String) values.get(i)));
      }
    } else {
      if (ValueValidation.isPrimitive(idea.getValue())) {
        if (idea.getValue().getClass().equals(Boolean.class)) {
          setWord(sdr, channel, 11, getArrayFromWords(String.valueOf(idea.getValue())));
        } else {
           setValue(sdr, channel, 11, columns, (Number) idea.getValue());
        }
      } else if (ValueValidation.isString(idea.getValue())) {
        if (idea.getValue() != null) {
          setWord(sdr, channel, 11, getArrayFromWords((String) idea.getValue()));
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

      setValue(sdr, channel, 7, columns, metadataValue);

      int length = 0;

      if (ValueValidation.isArray(idea.getValue())) {
        List values = idea.getValue() instanceof List ? (List) idea.getValue()
            : valueConverter.convertToList(idea.getValue());
        length = values.size();
      }

      setValue(sdr, channel, 9, columns, length);

    }
  }

  private void setNameValue(Idea idea, int[][][] sdr, int channel) {
    if (idea.getName() != null && getArrayFromWords(idea.getName()) != null) {
      setWord(sdr, channel, 4, getArrayFromWords(idea.getName()));
    }
  }

  private void setTypeValue(Idea idea, int[][][] sdr, int channel) {
    setValue(sdr, channel, 5, columns, (int) idea.getType());
  }

  private void setWord(int[][][] sdr, int channel, int row, int[] value) {
    sdr[channel][row] = value;
  }


  private void setValue(int[][][] sdr, int channel, int row, int length, Number value) {
    if(toRaw) {
      setNumericValueToRaw(sdr, channel, row, length, value);
    } else {
      setNumericValue(sdr, channel, row, length, value);
    }
  }

  private void setNumericValueToRaw(int[][][] sdr, int channel, int row, int length, Number value) {

    double doubleValue = value.doubleValue();

    String stringValue = buildValueString(doubleValue, length);

    if(stringValue != null) {
      int offset = 0;
      for (int i = row; i <= row+1 ; i++) {
        for (int j = 0; j < length; j++) {
          sdr[channel][i][j] = Integer.parseInt(String.valueOf(stringValue.charAt(j+offset)));
        }
        offset+=length;
      }
    }
  }

  private String buildValueString(Number value, int length) {

    String stringValue = Long.toBinaryString(length == 32 ? Double.doubleToRawLongBits(value.doubleValue()) : Float.floatToRawIntBits(value.floatValue()));

    if(value.doubleValue() == 0d)
      return null;

    int fillCount = 32 * 2 - stringValue.length();

    if (fillCount > 0) {
      if (value.doubleValue() < 0d) {
        fillCount--;
        stringValue = "1" + StringUtils.repeat("0", fillCount) + stringValue;
      } else {
        stringValue = StringUtils.repeat("0", fillCount) + stringValue;
      }
    }

    return stringValue;
  }

  private void setNumericValue(int[][][] sdr, int channel, int row, int length, Number value) {
    int range = length / 2;

    List<Double> baseTenValue = valueConverter.convertNumberToBaseTen(
        Math.abs(value.doubleValue()));

    String valueString = String.format("%.2f", baseTenValue.get(0));
    valueString = valueString.replace(".", "");
    valueString = valueString.replace("-", "");

    int offset = 0;
    int interval = 0;

    for (int i = 0; i < Math.min(valueString.length(), 3); i++) {
      int valueInt = Integer.parseInt(String.valueOf(valueString.charAt(i)));
      int[] valueSDR = getArrayFromValues(valueInt, range);

      for (int j = 0; j < valueSDR.length; j++) {
        sdr[channel][row+offset][interval * range + j] = valueSDR[j];
      }

      if((i + 1) * range >= length) {
        offset++;
        interval = 0;
      } else {
        interval++;
      }
    }

    int base = baseTenValue.get(1).intValue();

    int[] baseSDR = getArrayFromBaseValues(Math.abs(base), range/2);
    for (int i = 0; i < baseSDR.length; i++) {
      sdr[channel][row + 1][range+i] = baseSDR[i];
    }

    int[] signalSDR = getArrayFromSignalValues(value.doubleValue() < 0? 1:0, range/4);
    for (int i = 0; i < signalSDR.length; i++) {
      sdr[channel][row + 1][range+baseSDR.length+i] = signalSDR[i];
    }

    int[] baseSignalSDR = getArrayFromSignalValues(base < 0? 1:0, range/4);
    for (int i = 0; i < baseSignalSDR.length; i++) {
      sdr[channel][row + 1][range+baseSDR.length+signalSDR.length+i] = baseSignalSDR[i];
    }
  }

  public int[] getArrayFromWords(String word) {
    if (getDictionary().getWords().containsKey(word)) {
      return getDictionary().getWords().get(word);
    } else {
      int[] wordSDR = generateWordContent(columns, getDictionary().getWords());
      getDictionary().getWords().put(word, wordSDR);
      return wordSDR;
    }
  }

  public int[] getArrayFromValues(Integer value, Integer length) {
    if (getDictionary().getValues().containsKey(value)) {
      return getDictionary().getValues().get(value);
    } else {
      int[] valueSDR = generateNumericContent(length, getDictionary().getValues());
      getDictionary().getValues().put(value, valueSDR);
      return valueSDR;
    }
  }

  public int[] getArrayFromBaseValues(Integer base, Integer length) {
    if (getDictionary().getBaseValues().containsKey(base)) {
      return getDictionary().getBaseValues().get(base);
    } else {
      int[] baseSDR = generateNumericContent(length, getDictionary().getBaseValues());
      getDictionary().getBaseValues().put(base, baseSDR);
      return baseSDR;
    }
  }

  public int[] getArrayFromSignalValues(Integer signal, Integer length) {
    if (getDictionary().getSignalValues().containsKey(signal)) {
      return getDictionary().getSignalValues().get(signal);
    } else {

      int[] signalSDR = new int[length];
      for (int i = 0; i < length; i++) {
        signalSDR[i] = signal;
      }

      getDictionary().getSignalValues().put(signal, signalSDR);
      return signalSDR;
    }
  }


  private int[] generateWordContent(int length, Map<String, int[]> map) {

    boolean retry = true;

    while (retry) {
      int[] value = generateSDR(length);

      retry = map.entrySet().stream()
              .filter(entry -> entry.getValue().length == length)
              .filter(entry -> compareSDR(entry.getValue(), value))
              .collect(Collectors.toList()).size() > 0 ;

      if (!retry) {
        return value;
      }
    }

    return initializeSDR(new int[length], getDefaultValue());
  }

  private int[] generateNumericContent(int length, Map<Integer, int[]> map) {

    boolean retry = true;

    while (retry) {
      int[] value = generateSDR(length);

      retry = map.entrySet().stream()
          .filter(entry -> entry.getValue().length == length)
          .filter(entry -> compareSDR(entry.getValue(), value))
          .collect(Collectors.toList()).size() > 0 ;

      if (!retry) {
        return value;
      }
    }

    return initializeSDR(new int[length], getDefaultValue());
  }


  private int[] generateSDR(int length) {

    Random random = new Random();

    int w = length / 2;

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



  private int[] initializeSDR(int[] value, int defaultValue) {
    for (int i = 0; i < value.length; i++) {
      value[i] = defaultValue;
    }
    return value;
  }

  private boolean compareSDR(int[] newValue, int[] value) {

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

  public Dictionary getDictionary() {
    return dictionary;
  }

  public void setDictionary(Dictionary dictionary) {
    this.dictionary = dictionary;
  }
}
