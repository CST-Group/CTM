package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.idea.model.PlanDataSample;
import br.unicamp.ctm.representation.idea.model.SDRDataSample;
import br.unicamp.ctm.representation.model.Dictionary;
import br.unicamp.ctm.representation.model.SDRIdea;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateDataFromIdeaSDRFromPlanTest {


  private static Dictionary dictionary;
  private static SDRIdeaSerializer sdrIdeaSerializer;
  private static SDRIdeaSerializer sdrIdeaSerializerControl;

  private static SDRIdeaDeserializer sdrIdeaDeserializer;

  @Before
  public void setup() throws FileNotFoundException {
    Gson gson = new Gson();

    File dictionaryFile = new File("/opt/repository/dataTrainingShortSDR/dictionary.json");

    if(dictionaryFile.exists())
      dictionary = gson.fromJson(new FileReader(dictionaryFile), Dictionary.class);
    else
      dictionary = new Dictionary();

    sdrIdeaSerializer = new SDRIdeaSerializer(15,32,32);
    sdrIdeaSerializer.setDictionary(dictionary);

    sdrIdeaDeserializer = new SDRIdeaDeserializer(dictionary);
  }

  public static void main(String[] args) throws Exception {

    Gson gson = new Gson();

    File dictionaryFile = new File("/opt/repository/dataTrainingShortSDR/dictionary.json");

    if(dictionaryFile.exists())
      dictionary = gson.fromJson(new FileReader(dictionaryFile), Dictionary.class);
    else
      dictionary = new Dictionary();

    sdrIdeaSerializer = new SDRIdeaSerializer(10,32,32);
    sdrIdeaSerializer.setDictionary(dictionary);

    testNewGenerateDataFile();
  }

  public static void testNewGenerateDataFile() throws Exception {
    int j = 0;

    Gson gson = new Gson();
    List<PlanDataSample> dataSamples = new ArrayList<>();

    for (int k = 0; k < 1796; k++) {

      File goalFile = new File("/opt/repository/dataTrainingIdea/plan/initGoalIdeaFile"+k+".json");
      Idea[] goalIdeas = gson.fromJson(new FileReader(goalFile), Idea[].class);

      for (int i = 0; i < goalIdeas.length; i++) {
        resetIdeaIds(goalIdeas[i], -1);

        List controlList = ((List) goalIdeas[i].getValue()).subList(0, ((List) goalIdeas[i].getValue()).size());

        int[] control = new int[17];
        for (int t = 0; t < controlList.size(); t++) {
          control[t] = ((Double) controlList.get(t)).intValue();
        }

        goalIdeas[i].setValue("");

        SDRIdea goalSDRIdea = sdrIdeaSerializer.serialize(goalIdeas[i]);

        int[][][][] goal = new int[1][][][];
        goal[0] = goalSDRIdea.getSdr();

        dataSamples.add(new PlanDataSample(goal, control));

        if (dataSamples.size() == 100) {
          dataSamples = saveDataSamplesInFile(j, gson, dataSamples);
          j++;
        }
      }
    }

    if(dataSamples.size() > 0) {
      saveDataSamplesInFile(j, gson, dataSamples);
    }

  }

  @NotNull
  private static List<PlanDataSample> saveDataSamplesInFile(int j, Gson gson, List<PlanDataSample> dataSamples) throws IOException {
    System.out.println("Saving dataTestPlanSDR_" + j + ".json!");
    String json = gson.toJson(dataSamples);

    FileWriter fileWriter = new FileWriter(
            "/opt/repository/dataTestPlanSDR/dataTestPlanSDR_" + j + ".json");
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    bufferedWriter.write(json);
    bufferedWriter.close();

    System.out.println("dataTestPlanSDR_" + j + ".json Saved!");

    dataSamples = new ArrayList<>();
    return dataSamples;
  }

  @Test
  public void testGenerateDataFile() throws Exception {
    int j = 0;

    Gson gson = new Gson();

    for (int k = 1; k <= 5; k++) {

      File planFile = new File("./src/test/resources/plansShort"+k+".json");
      File currentStateFile = new File("./src/test/resources/currentStatesShort"+k+".json");
//      File goalFile = new File("./src/test/resources/goalsShort" + k + ".json");

      Idea[] planIdeas = gson.fromJson(new FileReader(planFile), Idea[].class);
//      Idea[] goalIdeas = gson.fromJson(new FileReader(goalFile), Idea[].class);
      Idea[] currentStateIdeas = gson.fromJson(new FileReader(currentStateFile), Idea[].class);

      List<SDRDataSample> dataSamples = new ArrayList<>();

      for (int i = 0; i < planIdeas.length; i++) {

        resetIdeaIds(planIdeas[i], -1);
//        resetIdeaIds(goalIdeas[i], -1);
        resetIdeaIds(currentStateIdeas[i], -1);

        SDRIdea planSDRIdea = sdrIdeaSerializer.serialize(planIdeas[i]);
//        SDRIdea goalSDRIdea = sdrIdeaSerializer.serialize(goalIdeas[i]);
        SDRIdea currentStateSDRIdea = sdrIdeaSerializer.serialize(currentStateIdeas[i]);

        int[][][][] x = new int[1][][][];

        x[0] = currentStateSDRIdea.getSdr();
//        x[1] = goalSDRIdea.getSdr();

        //dataSamples.add(new SDRDataSample(x, planSDRIdea.getSdr(), new int[1]));

        if (dataSamples.size() == 100) {
          System.out.println("Saving dataTrainingShortSDR_" + j + ".json!");
          String json = gson.toJson(dataSamples);

          FileWriter fileWriter = new FileWriter(
              "/opt/repository/dataTrainingShortSDR/dataTrainingShortSDR_" + j + ".json");
          BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

          bufferedWriter.write(json);
          bufferedWriter.close();

          System.out.println("dataTrainingShortSDR_" + j + ".json Saved!");

          dataSamples = new ArrayList<>();

          j++;
        }
      }
    }

    System.out.println("Saving dictionary.json!");

    FileWriter fileWriter = new FileWriter("/opt/repository/dataTrainingShortSDR/dictionary.json");
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    bufferedWriter.write(gson.toJson(sdrIdeaSerializer.getDictionary()));
    bufferedWriter.close();

    System.out.println("dictionary.json Saved!");
  }

  private static Integer resetIdeaIds(Idea idea, Integer value) {
    value = value + 1;
    idea.setId(value);

    for (Idea childIdea : idea.getL()) {
      value = resetIdeaIds(childIdea, value);
    }

    return value;
  }

  private static int[][][] extractSDRChannel(int[][][] sdr, int channel, int row, int column) {

    int[][][] newSDR = new int[1][row][column];

    for (int i = 0; i < row; i++) {
      for (int j = 0; j < column; j++) {
        newSDR[0][i][j] = sdr[channel][i][j];
      }
    }

    return newSDR;
  }
}
