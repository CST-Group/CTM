package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.model.MatrixIdea;
import java.lang.reflect.Array;

public class MatrixIdeaBuilder<T> {

  private final Class<T> clazz;

  public MatrixIdeaBuilder(Class<T> clazz) {
    this.clazz = clazz;
  }

  public MatrixIdea build(int rows, int columns, int size, T value) {
    T[][] matrix = (T[][]) Array.newInstance(clazz, rows, columns + size + 4);
    initializeMatrix(matrix, value);

    MatrixIdea matrixIdea = new MatrixIdea<T>(matrix);
    matrixIdea.setDefaultValue(value);

    return matrixIdea;
  }

  private T[][] initializeMatrix(T[][] matrix, T value) {

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        matrix[i][j] = value;
      }
    }

    return matrix;
  }

}
