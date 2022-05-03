package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.idea.model.DataSample;
import br.unicamp.ctm.representation.model.MatrixIdea;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GenerateDataFromIdeaTest {

  private MatrixIdeaSerializer matrixIdeaSerializer;

  @Before
  public void setup() {
    matrixIdeaSerializer = new MatrixIdeaSerializer(60, 60, 7);
  }


  @Test
  public void testGenerateDataFile() throws Exception {

    File planFile = new File("./src/test/resources/plans.json");
    File currentStateFile = new File("./src/test/resources/currentStates.json");
    File goalFile = new File("./src/test/resources/goals.json");

    Gson gson = new Gson();

    Idea[] planIdeas = gson.fromJson(new FileReader(planFile), Idea[].class);
    Idea[] goalIdeas = gson.fromJson(new FileReader(goalFile), Idea[].class);
    Idea[] currentStateIdeas = gson.fromJson(new FileReader(currentStateFile), Idea[].class);

    List<DataSample> dataSamples = new ArrayList<>();

    for (int i = 0; i < planIdeas.length; i++) {

      resetIdeaIds(planIdeas[i], -1);
      resetIdeaIds(goalIdeas[i], -1);
      resetIdeaIds(currentStateIdeas[i], -1);

      MatrixIdea planMatrixIdea = matrixIdeaSerializer.serialize(planIdeas[i]);
      MatrixIdea goalMatrixIdea = matrixIdeaSerializer.serialize(goalIdeas[i]);
      MatrixIdea currentStateMatrixIdea = matrixIdeaSerializer.serialize(currentStateIdeas[i]);

      double[][][] x = new double[2][][];

      x[0] = currentStateMatrixIdea.getMatrix();
      x[1] = goalMatrixIdea.getMatrix();

      dataSamples.add(new DataSample(x, planMatrixIdea.getMatrix()));
    }

    gson.toJson(dataSamples, new FileWriter("./src/test/resources/dataTraining.json"));

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
