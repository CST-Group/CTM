package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.model.MatrixIdea;

public class MatrixIdeaBuilder {

  public static MatrixIdea build(int rows, int columns, int size, double value) {
    double[][] matrix = new double[rows][columns + size + 4];
    initializeMatrix(matrix, value);

    MatrixIdea matrixIdea = new MatrixIdea(matrix);
    matrixIdea.setDefaultValue(value);

    return matrixIdea;
  }

  private static double[][] initializeMatrix(double[][] matrix, double value) {

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        matrix[i][j] = value;
      }
    }

    return matrix;
  }

}
