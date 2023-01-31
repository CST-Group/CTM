package br.unicamp.ctm.representation.validation;

import java.util.List;

public class ValueValidation {


  public static boolean isArray(Object object) {
    return object instanceof int[]
        || object instanceof double[]
        || object instanceof long[]
        || object instanceof float[]
        || object instanceof short[]
        || object instanceof boolean[]
        || object instanceof List;
  }

  public static boolean isArray(Class clazz) {
    return clazz.getCanonicalName().equals(int[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(double[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(long[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(float[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(short[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(boolean[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(Integer[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(Double[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(Long[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(Float[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(Short[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(Boolean[].class.getCanonicalName())
        || clazz.getCanonicalName().equals(String[].class.getCanonicalName());
  }

  public static boolean isPrimitive(Object object) {
    if (object == null) {
      return true;
    }
    if (object.getClass().isPrimitive()) {
      return true;
    }
    if (object instanceof Integer ||
        object instanceof Long ||
        object instanceof Double ||
        object instanceof Float ||
        object instanceof Boolean ||
        object instanceof Short ||
        object instanceof Byte) {
      return true;
    }
    return false;
  }

  public static boolean isPrimitive(Class clazz) {
    if (clazz == null) {
      return false;
    }
    if (clazz.isPrimitive()) {
      return true;
    }
    if (clazz.getCanonicalName().equals(Integer.class.getCanonicalName()) ||
        clazz.getCanonicalName().equals(Long.class.getCanonicalName()) ||
        clazz.getCanonicalName().equals(Double.class.getCanonicalName()) ||
        clazz.getCanonicalName().equals(Float.class.getCanonicalName()) ||
        clazz.getCanonicalName().equals(Boolean.class.getCanonicalName()) ||
        clazz.getCanonicalName().equals(Short.class.getCanonicalName()) ||
        clazz.getCanonicalName().equals(Byte.class.getCanonicalName())) {
      return true;
    }
    return false;
  }


  public static boolean isString(Object object) {
    return object instanceof String;
  }

  public static boolean isString(Class clazz) {
    return clazz.getCanonicalName().equals(String.class.getCanonicalName());
  }

  public boolean compareValue(int[] newValue, int[] value) {

    if(newValue.length == value.length) {

      for (int i = 0; i < newValue.length; i++) {
        if(newValue[i] != value[i]) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

}
