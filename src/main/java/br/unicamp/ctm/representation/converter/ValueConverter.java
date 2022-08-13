package br.unicamp.ctm.representation.converter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValueConverter<T> {

  public double[] extractDoubleArray(T[][] matrix, int length, int i) {
    double[] value = new double[length];
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (double)matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public int[] extractIntArray(T[][] matrix, int length, int i) {
    int[] value = new int[length];;
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (int) matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public float[] extractFloatArray(T[][] matrix, int length, int i) {
    float[] value = new float[length];;
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (float) matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public long[] extractLongArray(T[][] matrix, int length, int i) {
    long[] value = new long[length];
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (long) matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public short[] extractShortArray(T[][] matrix, int length, int i) {
    short[] value = new short[length];
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (short) matrix[i][k + matrix.length + 4];
      }
    }
    return value;
  }

  public boolean[] extractBooleanArray(T[][] matrix, int length, int i) {
    boolean[] value = new boolean[length];
    for (int k = 0; k < value.length; k++) {
      if (k + matrix.length + 4 < matrix[i].length) {
        value[k] = (int)matrix[i][k + matrix.length + 4] == 1d;
      }
    }
    return value;
  }

  public double[] convertToDoubleArray(Object object) {

    List list = convertToList(object);
    double[] value = new double[list.size()];

    for (int i = 0; i < list.size(); i++) {
      value[i] = (double) list.get(i);
    }

    return value;
  }

  public T[] convertToGenericArray(Object object) {

    List list = convertToList(object);

    T[] value = (T[]) Array.newInstance(getClass().getGenericSuperclass().getClass(), list.size());
    for (int i = 0; i < list.size(); i++) {
      value[i] = (T) list.get(i);
    }

    return value;
  }

  public List convertToList(Object object) {

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

  public List<Double> convertNumberToBaseTen(double value) {

    List<Double> numberBaseTen = new ArrayList<>();

    value = Math.abs(value);

    int base = 0;
    double valueDivided = value;

    while(true) {

      if(value == 0 || value == 1) {
        numberBaseTen.add(value);
        numberBaseTen.add(0d);

        return numberBaseTen;
      }
      if(value > 1) {
        if(valueDivided >= 1) {
          valueDivided/=10;
          base++;
        } else {
          numberBaseTen.add(valueDivided*10);
          numberBaseTen.add((double) (base-1));

          return numberBaseTen;
        }
      } else if(value < 1) {
        if(valueDivided <= 1) {
          valueDivided*=10;
          base--;
        } else {
          numberBaseTen.add(valueDivided/10);
          numberBaseTen.add((double) (base+1));

          return numberBaseTen;
        }
      }
    }
  }

}
