package br.unicamp.ctm.representation.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValueConverter {

  public static double[] extractDoubleArray(double[][] matrix, int length, int i) {
    double[] value = new double[length];
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public static int[] extractIntArray(double[][] matrix, int length, int i) {
    int[] value = new int[length];;
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (int) matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public static float[] extractFloatArray(double[][] matrix, int length, int i) {
    float[] value = new float[length];;
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (float) matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public static long[] extractLongArray(double[][] matrix, int length, int i) {
    long[] value = new long[length];
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (long) matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public static short[] extractShortArray(double[][] matrix, int length, int i) {
    short[] value = new short[length];
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (short) matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public static boolean[] extractBooleanArray(double[][] matrix, int length, int i) {
    boolean[] value = new boolean[length];
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = matrix[i][k + matrix.length + 4] == 1d;
      }
    }
    return value;
  }

  public static double[] convertToDoubleArray(Object object) {

    List list = convertToList(object);
    double[] value = new double[list.size()];

    for (int i = 0; i < list.size(); i++) {
      value[i] = (double) list.get(i);
    }

    return value;
  }


  public static List convertToList(Object object) {

    List list = new ArrayList();
    if (object instanceof int[]) {
      list = Arrays.stream((int[]) object).boxed().collect(Collectors.toList());
    } else if (object instanceof boolean[]) {
      for (boolean b : (boolean[]) object) {
        list.add(b ? 1d : 0d);
      }
    } else if (object instanceof float[]) {
      for (float b : (float[]) object) {
        list.add((double) b);
      }
    } else if (object instanceof short[]) {
      for (short b : (short[]) object) {
        list.add((double) b);
      }
    } else if (object instanceof double[] || object instanceof float[]) {
      list = Arrays.stream((double[]) object).boxed().collect(Collectors.toList());
    } else if (object instanceof long[]) {
      for (long b : (long[]) object) {
        list.add((double) b);
      }
    }

    return list;
  }

}
