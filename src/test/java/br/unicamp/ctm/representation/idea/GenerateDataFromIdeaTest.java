package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.idea.model.DataSample;
import br.unicamp.ctm.representation.model.MatrixIdea;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.description.field.FieldDescription.InGenericShape;
import org.junit.Before;
import org.junit.Test;

public class GenerateDataFromIdeaTest {

  private MatrixIdeaSerializer<Double> matrixIdeaSerializer;

  @Before
  public void setup() {
    matrixIdeaSerializer = new MatrixIdeaSerializer<>(Double.class, 69, 69, 7); 
  }

  @Test
  public void testGenerateDataFile() throws Exception {

    File planFile = new File("./src/test/resources/plans2.json");
    File currentStateFile = new File("./src/test/resources/currentStates2.json");
    File goalFile = new File("./src/test/resources/goals2.json");

    Gson gson = new Gson();

    Idea[] planIdeas = gson.fromJson(new FileReader(planFile), Idea[].class);
    Idea[] goalIdeas = gson.fromJson(new FileReader(goalFile), Idea[].class);
    Idea[] currentStateIdeas = gson.fromJson(new FileReader(currentStateFile), Idea[].class);

    List<DataSample<Double>> dataSamples = new ArrayList<>();

    for (int i = 0; i < planIdeas.length; i++) {

      resetIdeaIds(planIdeas[i], -1);
      resetIdeaIds(goalIdeas[i], -1);
      resetIdeaIds(currentStateIdeas[i], -1);

      MatrixIdea planMatrixIdea = matrixIdeaSerializer.serialize(planIdeas[i], 0d, 1d, false);
      MatrixIdea goalMatrixIdea = matrixIdeaSerializer.serialize(goalIdeas[i], 0d, 1d, false);
      MatrixIdea currentStateMatrixIdea = matrixIdeaSerializer.serialize(currentStateIdeas[i], 0d, 1d, false);

      Double[][][] x = new Double[2][][];

      x[0] = (Double[][]) currentStateMatrixIdea.getMatrix();
      x[1] = (Double[][]) goalMatrixIdea.getMatrix();

      dataSamples.add(new DataSample(x, planMatrixIdea.getMatrix()));
    }

    int mountByFile = 300;

    for (int i = 0; i <dataSamples.size()/mountByFile; i++) {
      String json = gson.toJson(dataSamples.subList(i*mountByFile, (i+1) * mountByFile));

      FileWriter fileWriter = new FileWriter("./src/test/resources/dataTraining_" + (6+i) + ".json");
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

      bufferedWriter.write(json);
      bufferedWriter.close();
    }

  }

  private Integer resetIdeaIds(Idea idea, Integer value) {
    value = value + 1;
    idea.setId(value);

    for (Idea childIdea : idea.getL()) {
      value = resetIdeaIds(childIdea, value);
    }

    return value;
  }
}
