package br.unicamp.ctm.representation.idea;


import br.unicamp.ctm.representation.converter.ValueConverter;
import br.unicamp.ctm.representation.model.MatrixIdea;
import br.unicamp.ctm.representation.validation.ValueValidation;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class MatrixIdeaSerializer<T> {

  private final Class<T> clazz;
  private int size;
  private int rows;
  private int columns;
  private ValueConverter<T> valueConverter;
  private T defaultValue;
  private T activeValue;

    public MatrixIdeaSerializer(Class<T> clazz, int rows, int columns, int size) {
    this.size = size;
    this.rows = rows;
    this.columns = columns;
    this.valueConverter = new ValueConverter<>();
    this.clazz = clazz;
    this.defaultValue = castToGeneric(0);
    this.activeValue = castToGeneric(1);
  }

  public MatrixIdea serialize(Idea idea, Map<String, Double> dictionary, T defaultValue, T activeValue,
      boolean bidirectional)
      throws Exception {
    MatrixIdea matrixIdea = new MatrixIdeaBuilder<T>(clazz).build(rows, columns, size, defaultValue);
    matrixIdea.setDictionary(dictionary);

    if (idea != null) {

      setNameValue(idea, matrixIdea, (T[][]) matrixIdea.getMatrix(), (int) idea.getId());
      setTypeValue((T[][]) matrixIdea.getMatrix(), castToGeneric(idea.getType()), (int) idea.getId());
      setMetadataValue(idea, (T[][]) matrixIdea.getMatrix(), castToGeneric(matrixIdea.getDefaultValue()), (int) idea.getId());
      valueAnalyse(matrixIdea, idea, (T[][]) matrixIdea.getMatrix(), (int) idea.getId());

      matrixIdea.setMatrix(generateMatrix(matrixIdea, idea, (T[][])  matrixIdea.getMatrix(), activeValue, bidirectional));

    } else {
      throw new Exception("Idea Graph is null.");
    }

    return completeMatrix(matrixIdea);
  }

  public MatrixIdea serialize(Idea idea) throws Exception {
    return serialize(idea, new HashMap<>(), castToGeneric(0), castToGeneric(1), true);
  }

  public MatrixIdea serialize(Idea idea, T defaultValue, T activeValue) throws Exception {
    return serialize(idea, new HashMap<>(), castToGeneric(defaultValue), castToGeneric(activeValue), true);
  }

  public MatrixIdea serialize(Idea idea, T defaultValue, T activeValue, boolean bidirectional) throws Exception {
    return serialize(idea, new HashMap<>(), castToGeneric(defaultValue), castToGeneric(activeValue), bidirectional);
  }

  public MatrixIdea serialize(Idea idea, T defaultValue) throws Exception {
    return serialize(idea, new HashMap<>(), castToGeneric(defaultValue), castToGeneric(1), true);
  }

  public MatrixIdea serialize(Idea idea, T defaultValue, boolean bidirectional) throws Exception {
    return serialize(idea, new HashMap<>(), castToGeneric(defaultValue), castToGeneric(1), bidirectional);
  }

  public MatrixIdea serialize(Idea idea, boolean bidirectional) throws Exception {
    return serialize(idea, new HashMap<>(), castToGeneric(0), castToGeneric(1), bidirectional);
  }

  public MatrixIdea serialize(Idea idea, Map<String, Double> dictionary, boolean bidirectional)
      throws Exception {
    return serialize(idea, dictionary, castToGeneric(0), castToGeneric(1), bidirectional);
  }

  public MatrixIdea serialize(Idea idea, Map<String, Double> dictionary) throws Exception {
    return serialize(idea, dictionary, castToGeneric(0), castToGeneric(1), true);
  }

  private T[][] generateMatrix(MatrixIdea matrixIdea, Idea idea, T[][] matrix, T activeValue,
      boolean bidirectional) {

    for (Idea childIdea : idea.getL()) {

      setLinkValue(matrix, (int) idea.getId(), (int) childIdea.getId(), activeValue, bidirectional);
      setNameValue(childIdea, matrixIdea, matrix, (int) childIdea.getId());
      setTypeValue(matrix, castToGeneric(childIdea.getType()), (int) childIdea.getId());
      setMetadataValue(childIdea, matrix, castToGeneric(matrixIdea.getDefaultValue()), (int) childIdea.getId());
      valueAnalyse(matrixIdea, childIdea, matrix, (int) childIdea.getId());

      generateMatrix(matrixIdea, childIdea, matrix, activeValue, bidirectional);
    }

    return matrix;
  }

  private void setNameValue(Idea idea, MatrixIdea<T> matrixIdea, T[][] matrix, int i) {
    if (idea.getName() != null && matrixIdea.getValueFromDictionary(idea.getName()) != null) {
      setValue(matrix, i, columns, castToGeneric(matrixIdea.getValueFromDictionary(idea.getName())), false);
    }
  }

  private void setMetadataValue(Idea idea, T[][] matrix, T defaultValue, int i) {
    if (idea.getValue() != null) {
      Integer metadataValue = IdeaMetadataValues.getMetadataMap()
          .get(idea.getValue().getClass());
      setValue(matrix, i, columns + 2,
          castToGeneric(metadataValue != null ? metadataValue : defaultValue), false);

      int length = 0;

      if (ValueValidation.isArray(idea.getValue())) {
        T[] values = valueConverter.convertToGenericArray(idea.getValue());
        length = values.length;
      }

      setValue(matrix, i, columns + 3, castToGeneric(length), false);
    }
  }

  private void setTypeValue(T[][] matrix, T type, int i) {
    setValue(matrix, i, columns + 1, type, false);
  }

  private void setLinkValue(T[][] matrix, int i, int j, T activeValue, boolean bidirectional) {
    setValue(matrix, i, j, castToGeneric(activeValue), bidirectional);
  }

  private void valueAnalyse(MatrixIdea<T> matrixIdea, Idea idea, T[][] matrix, int childIdeaId) {
    if (ValueValidation.isArray(idea.getValue())) {
      T[] values = valueConverter.convertToGenericArray(idea.getValue());
      for (int i = 0; i < values.length; i++) {
        setValue(matrix, childIdeaId, columns + 4 + i, values[i],
            false);
      }
    } else {
      if (ValueValidation.isPrimitive(idea.getValue())) {
        if(idea.getValue().getClass().equals(Boolean.class)) {
          setValue(matrix, childIdeaId, columns + 4, castToGeneric((Boolean) idea.getValue() ? 1d: 0d), false);
        } else {
          setValue(matrix, childIdeaId, columns + 4, castToGeneric(idea.getValue()), false);
        }
      } else if (ValueValidation.isString(idea.getValue())) {
        if (matrixIdea.getDictionary().get((String) idea.getValue()) != null) {
          Integer value = matrixIdea.getDictionary().get((String) idea.getValue());
          if (value != null) {
            setValue(matrix, childIdeaId, columns + 4, castToGeneric(value), false);
          }
        }
      }
    }
  }

  private void setValue(T[][] matrix, int i, int j, T value,
      boolean bidirectional) {
    matrix[i][j] = value;

    if (bidirectional) {
      matrix[j][i] = value;
    }
  }

  private MatrixIdea<T> completeMatrix(MatrixIdea<T> matrixIdea) {

    int row = matrixIdea.getMatrix().length;
    int columns = matrixIdea.getMatrix()[0].length;

    T[][] newMatrix = (T[][]) Array.newInstance(clazz, columns, columns);

    for (int i = 0; i < columns; i++) {
      for (int j = 0; j < columns; j++) {
        if(i < row) {
          newMatrix[i][j] = matrixIdea.getMatrix()[i][j];
        } else {
          newMatrix[i][j] = castToGeneric(0d);
        }
      }
    }

    matrixIdea.setMatrix(newMatrix);

    return matrixIdea;
  }

  private T castToGeneric(Object object) {

    if (clazz.equals(Integer.class)) {
      return clazz.cast(((Number)object).intValue());
    } else if (clazz.equals(Double.class)) {
      return clazz.cast(((Number)object).doubleValue());
    } else if (clazz.equals(Float.class)) {
      return clazz.cast(((Number)object).floatValue());
    } else if (clazz.equals(Long.class)) {
      return clazz.cast(((Number)object).longValue());
    } else if (clazz.equals(Short.class)) {
      return clazz.cast(((Number)object).shortValue());
    }

    return clazz.cast(object);

  }
}
