package br.unicamp.ctm.representation.idea;

import java.util.HashMap;
import java.util.Map;
import scala.Char;

public class MatrixIdeaMetadataValues {

  public static Map<Class, Double> getMetadataMap() {

    HashMap<Class, Double> metadataMap = new HashMap<>();

    metadataMap.put(int.class, 1d);
    metadataMap.put(Integer.class, 1d);
    metadataMap.put(float.class, 2d);
    metadataMap.put(Float.class, 2d);
    metadataMap.put(double.class, 3d);
    metadataMap.put(Double.class, 2d);
    metadataMap.put(char.class, 4d);
    metadataMap.put(Char.class, 4d);
    metadataMap.put(short.class, 5d);
    metadataMap.put(Short.class, 5d);
    metadataMap.put(boolean.class, 6d);
    metadataMap.put(Boolean.class, 6d);
    metadataMap.put(String.class, 7d);

    metadataMap.put(int[].class, 8d);
    metadataMap.put(Integer[].class, 8d);
    metadataMap.put(double[].class, 9d);
    metadataMap.put(Double[].class, 9d);
    metadataMap.put(float[].class, 10d);
    metadataMap.put(Float[].class, 10d);
    metadataMap.put(short[].class, 11d);
    metadataMap.put(Short[].class, 11d);
    metadataMap.put(long[].class, 12d);
    metadataMap.put(Long[].class, 12d);
    metadataMap.put(boolean[].class, 13d);
    metadataMap.put(Boolean[].class, 13d);
    metadataMap.put(String[].class, 14d);

    return metadataMap;
  }
}
