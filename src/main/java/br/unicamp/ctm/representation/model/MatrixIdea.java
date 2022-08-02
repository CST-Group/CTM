package br.unicamp.ctm.representation.model;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class MatrixIdea<T> {

  private T[][] matrix;
  private Map<String, Integer> dictionary;
  private T defaultValue;

  public MatrixIdea(T[][] matrix) {
    this.setMatrix(matrix);
    this.setDictionary(new HashMap<>());
  }

  public MatrixIdea(T[][] matrix, Map<String, Integer> dictionary) {
    this.setMatrix(matrix);
    this.setDictionary(dictionary);
  }

  public MatrixIdea(int rows, int columns) {
    this.setMatrix((T[][]) Array.newInstance(getClass().getGenericSuperclass().getClass(), rows, columns));
  }

  public MatrixIdea(int rows, int columns, Map<String, Integer> dictionary) {
    this.setMatrix((T[][]) Array.newInstance(getClass().getGenericSuperclass().getClass(), rows, columns));
    this.setDictionary(dictionary);
  }

  public T[][] getMatrix() {
    return matrix;
  }

  public void setMatrix(T[][] matrix) {
    this.matrix = matrix;
  }

  public Map<String, Integer> getDictionary() {
    return dictionary;
  }

  public void setDictionary(Map<String, Integer> dictionary) {
    this.dictionary = dictionary;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; i < matrix.length; i++) {
      stringBuilder.append(i + " |\t");

      for (int j = 0; j < matrix[i].length; j++) {

        stringBuilder.append(String.format("%3.2f", matrix[i][j]));
        stringBuilder.append("\t");

        if (j + 1 == matrix[i].length) {
          stringBuilder.append("|\n");
        }
      }
    }

    stringBuilder.append("\n");

    return stringBuilder.toString();
  }

  public T getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(T defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Integer getValueFromDictionary(String word) {

    if (getDictionary().containsKey(word)) {
      return getDictionary().get(word);
    } else {
      if (!getDictionary().isEmpty()) {
        Integer value = getDictionary().entrySet().stream()
            .max((wordEntry1, wordEntry2) -> wordEntry1.getValue() > wordEntry2.getValue() ? 1 : -1)
            .get()
            .getValue();
        getDictionary().put(word, value + 1);

        return value + 1;
      } else {
        getDictionary().put(word, 0);

        return 0;
      }
    }
  }
}
