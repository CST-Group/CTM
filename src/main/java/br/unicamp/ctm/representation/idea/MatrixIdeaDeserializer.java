package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.converter.ValueConverter;
import br.unicamp.ctm.representation.model.MatrixIdea;
import br.unicamp.ctm.representation.validation.ValueValidation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class MatrixIdeaDeserializer<T> {

  private ValueConverter valueConverter;

  public MatrixIdeaDeserializer() {
    valueConverter = new ValueConverter();
  }

  public Idea deserialize(MatrixIdea matrixIdea) {

    List<Idea> ideaList = new ArrayList<>();

    generateIdeaGraph(matrixIdea, ideaList);

    return ideaList.stream().findFirst().get();
  }

  private void generateIdeaGraph(MatrixIdea matrixIdea, List<Idea> ideaList) {

    T[][] matrix = (T[][]) matrixIdea.getMatrix();
    Map<String, Integer> dictionary = matrixIdea.getDictionary();

    for (int i = 0; i < matrix.length; i++) {
      Idea idea = null;

      int finalI = i;

      Optional<Entry<String, Integer>> entryOptional = dictionary.entrySet().stream()
          .filter(entry -> entry.getValue().equals(matrix[finalI][matrix.length]))
          .findFirst();

      if(entryOptional.isPresent()) {
        idea = getIdea(ideaList, entryOptional.get().getKey(), i);
      } else {
        idea = getIdea(ideaList,  "IDEA_" + i, i);
      }

      setValue(idea, matrix, matrixIdea.getDictionary(), i);
      setType(idea, matrix, i);

      for (int j = 0; j < matrix.length; j++) {

        if ((int)matrix[i][j]  != 0) {

          Idea childIdea = null;

          int finalJ = j;


          entryOptional = dictionary.entrySet().stream()
              .filter(entry -> entry.getValue().equals(matrix[finalJ][matrix.length]))
              .findFirst();

          if(entryOptional.isPresent()) {
            childIdea = getIdea(ideaList, entryOptional.get().getKey(), j);
          } else {
            childIdea = getIdea(ideaList,  "IDEA_" + j, j);
          }

          setValue(childIdea, matrix, matrixIdea.getDictionary(), j);
          setType(childIdea, matrix, j);

          idea.add(childIdea);
        }
      }
    }
  }

  private void setType(Idea idea, T[][] matrix, int i) {
    idea.setType((int) matrix[i][matrix.length + 1]);
  }

  private void setValue(Idea idea, T[][] matrix, Map<String, Integer> dictionary, int i) {
    Optional<Entry<Class, Integer>> entryOptional = MatrixIdeaMetadataValues.getMetadataMap()
        .entrySet().stream().filter(entry -> entry.getValue() == matrix[i][matrix.length + 2])
        .findFirst();

    if (entryOptional.isPresent()) {
      Class clazz = entryOptional.get().getKey();

      if (ValueValidation.isArray(clazz)) {

        int length = (int) matrix[i][matrix.length + 3];

        if (clazz.getCanonicalName().equals(double[].class.getCanonicalName())) {
          idea.setValue(valueConverter.extractDoubleArray(matrix, length, i));
        } else if (clazz.getCanonicalName().equals(int[].class.getCanonicalName())) {
          idea.setValue(valueConverter.extractIntArray(matrix, length, i));
        } else if (clazz.getCanonicalName().equals(float[].class.getCanonicalName())) {
          idea.setValue(valueConverter.extractFloatArray(matrix, length, i));
        } else if (clazz.getCanonicalName().equals(short[].class.getCanonicalName())) {
          idea.setValue(valueConverter.extractShortArray(matrix, length, i));
        } else if (clazz.getCanonicalName().equals(boolean[].class.getCanonicalName())) {
          idea.setValue(valueConverter.extractBooleanArray(matrix, length, i));
        } else if (clazz.getCanonicalName().equals(long[].class.getCanonicalName())) {
          idea.setValue(valueConverter.extractLongArray(matrix, length, i));
        }

      } else if (ValueValidation.isPrimitive(clazz)) {

        if (clazz.getCanonicalName().equals(int.class.getCanonicalName())) {
          idea.setValue((int) matrix[i][matrix.length + 4]);
        } else if (clazz.getCanonicalName().equals(long.class.getCanonicalName())) {
          idea.setValue((long) matrix[i][matrix.length + 4]);
        } else if (clazz.getCanonicalName().equals(double.class.getCanonicalName())) {
          idea.setValue(matrix[i][matrix.length + 4]);
        } else if (clazz.getCanonicalName().equals(float.class.getCanonicalName())) {
          idea.setValue((float) matrix[i][matrix.length + 4]);
        } else if (clazz.getCanonicalName().equals(boolean.class.getCanonicalName())) {
          idea.setValue((int)matrix[i][matrix.length + 4] == 1);
        } else if (clazz.getCanonicalName().equals(short.class.getCanonicalName())) {
          idea.setValue((short) matrix[i][matrix.length + 4]);
        } else if (clazz.getCanonicalName().equals(byte.class.getCanonicalName())) {
          idea.setValue((byte) matrix[i][matrix.length + 4]);
        }

      } else if (ValueValidation.isString(clazz)) {

        Optional<Entry<String, Integer>> entryValueOptional = dictionary.entrySet().stream()
            .filter(entry -> entry.getValue().equals(matrix[i][matrix.length + 4])).findFirst();

        entryValueOptional.ifPresent(
            entry -> idea.setValue(entry.getKey()));
      }
    }
  }

  private Idea getIdea(List<Idea> ideaList, String name, int id) {
    Idea childIdea = null;

    if (ideaList.stream().anyMatch(idea -> idea.getName().equals(name))) {
      childIdea = ideaList.stream().filter(idea -> idea.getName().equals(name)).findFirst().get();
    } else {
      childIdea = new Idea(name);
      childIdea.setId(id);
      ideaList.add(childIdea);
    }

    return childIdea;
  }

  private T castToGeneric(Object object) {
    return (T) getClass().getGenericSuperclass().getClass().cast(object);
  }

}
