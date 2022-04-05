package br.unicamp.ctm.representation.model;

import java.util.HashMap;
import java.util.Map;

public class MatrixIdea {

  private double[][] matrix;
  private Map<String, Double> dictionary;
  private double defaultValue;

  public MatrixIdea(double[][] matrix) {
    this.setMatrix(matrix);
    this.setDictionary(new HashMap<>());
  }

  public MatrixIdea(double[][] matrix, Map<String, Double> dictionary) {
    this.setMatrix(matrix);
    this.setDictionary(dictionary);
  }

  public MatrixIdea(int rows, int columns) {
    this.setMatrix(new double[rows][columns]);
  }

  public MatrixIdea(int rows, int columns, Map<String, Double> dictionary) {
    this.setMatrix(new double[rows][columns]);
    this.setDictionary(dictionary);
  }

  public double[][] getMatrix() {
    return matrix;
  }

  public void setMatrix(double[][] matrix) {
    this.matrix = matrix;
  }

  public Map<String, Double> getDictionary() {
    return dictionary;
  }

  public void setDictionary(Map<String, Double> dictionary) {
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

  public double getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(double defaultValue) {
    this.defaultValue = defaultValue;
  }
}
