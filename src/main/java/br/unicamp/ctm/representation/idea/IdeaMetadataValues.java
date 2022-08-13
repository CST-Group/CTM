package br.unicamp.ctm.representation.idea;

import java.util.HashMap;
import java.util.Map;
import scala.Char;

public class IdeaMetadataValues {

  public static Map<Class, Integer> getMetadataMap() {

    HashMap<Class, Integer> metadataMap = new HashMap<>();

    metadataMap.put(int.class, 1);
    metadataMap.put(Integer.class, 1);
    metadataMap.put(float.class, 2);
    metadataMap.put(Float.class, 2);
    metadataMap.put(double.class, 3);
    metadataMap.put(Double.class, 2);
    metadataMap.put(char.class, 4);
    metadataMap.put(Char.class, 4);
    metadataMap.put(short.class, 5);
    metadataMap.put(Short.class, 5);
    metadataMap.put(boolean.class, 6);
    metadataMap.put(Boolean.class, 6);
    metadataMap.put(String.class, 7);

    metadataMap.put(int[].class, 8);
    metadataMap.put(Integer[].class, 8);
    metadataMap.put(double[].class, 9);
    metadataMap.put(Double[].class, 9);
    metadataMap.put(float[].class, 10);
    metadataMap.put(Float[].class, 10);
    metadataMap.put(short[].class, 11);
    metadataMap.put(Short[].class, 11);
    metadataMap.put(long[].class, 12);
    metadataMap.put(Long[].class, 12);
    metadataMap.put(boolean[].class, 13);
    metadataMap.put(Boolean[].class, 13);
    metadataMap.put(String[].class, 14);

    return metadataMap;
  }
}
