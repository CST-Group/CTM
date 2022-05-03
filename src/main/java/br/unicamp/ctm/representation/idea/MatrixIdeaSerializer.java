package br.unicamp.ctm.representation.idea;


import br.unicamp.ctm.representation.converter.ValueConverter;
import br.unicamp.ctm.representation.model.MatrixIdea;
import br.unicamp.ctm.representation.validation.ValueValidation;
import java.util.HashMap;
import java.util.Map;

public class MatrixIdeaSerializer {

  private int size;
  private int rows;
  private int columns;

  public MatrixIdeaSerializer(int rows, int columns, int size) {
    this.size = size;
    this.rows = rows;
    this.columns = columns;
  }

  public MatrixIdea serialize(Idea idea, Map<String, Double> dictionary, double defaultValue,
      boolean bidirectional)
      throws Exception {
    MatrixIdea matrixIdea = MatrixIdeaBuilder.build(rows, columns, size, defaultValue);
    matrixIdea.setDictionary(dictionary);

    if (idea != null) {

      setNameValue(idea, matrixIdea, matrixIdea.getMatrix(), (int) idea.getId());
      setTypeValue(matrixIdea.getMatrix(), idea.getType(), (int) idea.getId());
      setMetadataValue(idea, matrixIdea.getMatrix(), matrixIdea.getDefaultValue(), (int) idea.getId());
      valueAnalyse(matrixIdea, idea, matrixIdea.getMatrix(), (int) idea.getId());

      matrixIdea.setMatrix(generateMatrix(matrixIdea, idea, matrixIdea.getMatrix(), bidirectional));

    } else {
      throw new Exception("Idea Graph is null.");
    }

    return matrixIdea;
  }

  public MatrixIdea serialize(Idea idea) throws Exception {
    return serialize(idea, new HashMap<>(), 0, true);
  }

  public MatrixIdea serialize(Idea idea, boolean bidirectional) throws Exception {
    return serialize(idea, new HashMap<>(), 0, bidirectional);
  }

  public MatrixIdea serialize(Idea idea, Map<String, Double> dictionary, boolean bidirectional)
      throws Exception {
    return serialize(idea, dictionary, 0, bidirectional);
  }

  public MatrixIdea serialize(Idea idea, Map<String, Double> dictionary) throws Exception {
    return serialize(idea, dictionary, 0, true);
  }

  private double[][] generateMatrix(MatrixIdea matrixIdea, Idea idea, double[][] matrix,
      boolean bidirectional) {

    for (Idea childIdea : idea.getL()) {

      setLinkValue(matrix, (int) idea.getId(), (int) childIdea.getId(), bidirectional);
      setNameValue(childIdea, matrixIdea, matrix, (int) childIdea.getId());
      setTypeValue(matrix, childIdea.getType(), (int) childIdea.getId());
      setMetadataValue(childIdea, matrix, matrixIdea.getDefaultValue(), (int) childIdea.getId());
      valueAnalyse(matrixIdea, childIdea, matrix, (int) childIdea.getId());

      generateMatrix(matrixIdea, childIdea, matrix, bidirectional);
    }

    return matrix;
  }

  private void setNameValue(Idea idea, MatrixIdea matrixIdea, double[][] matrix, int i) {
    if (idea.getName() != null && matrixIdea.getValueFromDictionary(idea.getName()) != null) {
      setValue(matrix, i, columns, matrixIdea.getValueFromDictionary(idea.getName()), false);
    }
  }

  private void setMetadataValue(Idea idea, double[][] matrix, double defaultValue, int i) {
    if (idea.getValue() != null) {
      Double metadataValue = MatrixIdeaMetadataValues.getMetadataMap()
          .get(idea.getValue().getClass());
      setValue(matrix, i, columns + 2,
          metadataValue != null ? metadataValue : defaultValue, false);

      int length = 0;

      if (ValueValidation.isArray(idea.getValue())) {
        double[] values = ValueConverter.convertToDoubleArray(idea.getValue());
        length = values.length;
      }

      setValue(matrix, i, columns + 3, length, false);
    }
  }

  private void setTypeValue(double[][] matrix, int type, int i) {
    setValue(matrix, i, columns + 1, type, false);
  }

  private void setLinkValue(double[][] matrix, int i, int j, boolean birectional) {
    setValue(matrix, i, j, 1, birectional);
  }

  private void valueAnalyse(MatrixIdea matrixIdea, Idea idea, double[][] matrix, int childIdeaId) {
    if (ValueValidation.isArray(idea.getValue())) {
      double[] values = ValueConverter.convertToDoubleArray(idea.getValue());
      for (int i = 0; i < values.length; i++) {
        setValue(matrix, childIdeaId, columns + 4 + i, values[i],
            false);
      }
    } else {
      if (ValueValidation.isPrimitive(idea.getValue())) {
        if(idea.getValue().getClass().equals(Boolean.class)) {
          setValue(matrix, childIdeaId, columns + 4, (Boolean) idea.getValue() ? 1d: 0d, false);
        } else {
          setValue(matrix, childIdeaId, columns + 4, (double) idea.getValue(), false);
        }
      } else if (ValueValidation.isString(idea.getValue())) {
        if (matrixIdea.getDictionary().get((String) idea.getValue()) != null) {
          Double value = matrixIdea.getDictionary().get((String) idea.getValue());
          if (value != null) {
            setValue(matrix, childIdeaId, columns + 4, value, false);
          }
        }
      }
    }
  }

  private void setValue(double[][] matrix, int i, int j, double value,
      boolean bidirectional) {
    matrix[i][j] = value;

    if (bidirectional) {
      matrix[j][i] = value;
    }
  }



}
