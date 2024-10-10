package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.idea.model.PlanDataSample;
import br.unicamp.ctm.representation.idea.model.SDRDataSample;
import br.unicamp.ctm.representation.idea.model.TransformerPlanDataSample;
import br.unicamp.ctm.representation.model.Dictionary;
import br.unicamp.ctm.representation.model.SDRIdea;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TransformerGenerateDataFromIdeaSDRFromPlanTest {


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

    sdrIdeaSerializer = new SDRIdeaSerializer(20,32,32);
    sdrIdeaSerializer.setDictionary(dictionary);

    sdrIdeaDeserializer = new SDRIdeaDeserializer(dictionary);
  }

  public static void main(String[] args) throws Exception {

    Gson gson = new Gson();

    File dictionaryFile = new File("/opt/repository/dataPlanSDR/dictionary.json");

    if(dictionaryFile.exists())
      dictionary = gson.fromJson(new FileReader(dictionaryFile), Dictionary.class);
    else
      dictionary = new Dictionary();

    //sdrIdeaSerializer = new SDRIdeaSerializer(20,32,32, false, true, 3, 4);
    sdrIdeaSerializer = new SDRIdeaSerializer(20,32,32);
    sdrIdeaSerializer.setDictionary(dictionary);

    testNewGenerateDataFile();

    System.out.println("Saving dictionary.json!");

    FileWriter fileWriter = new FileWriter("/opt/repository/dataPlanSDR/dictionary.json");
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    bufferedWriter.write(gson.toJson(sdrIdeaSerializer.getDictionary()));
    bufferedWriter.close();

    System.out.println("dictionary.json Saved!");

  }

  public static void testNewGenerateDataFile() throws Exception {
    int j = 0;

    Gson gson = new Gson();
    List<TransformerPlanDataSample> dataSamples = new ArrayList<>();

    for (int k = 0; k < 599; k++) {

      File planFile = new File("/opt/repository/dataTrainingIdea/plan/planIdeaFile"+k+".json");
      Idea[] planIdeas = gson.fromJson(new FileReader(planFile), Idea[].class);

      File goalFile = new File("/opt/repository/dataTrainingIdea/goal/goalIdeaFile"+k+".json");
      Idea[] goalIdeas = gson.fromJson(new FileReader(goalFile), Idea[].class);

      for (int i = 0; i < goalIdeas.length; i++) {
        Idea startIdeaPlan = new Idea("start", "");
        startIdeaPlan.setId(0);
        startIdeaPlan.add(planIdeas[i]);

        for (int l = 0; l < planIdeas[i].getL().size(); l++) {
          startIdeaPlan.add(planIdeas[i].getL().get(l));
        }

        //resetIdeaIds(goalIdeas[i], -1);
        //resetIdeaIds(planIdeas[i], -1);
        //resetIdeaIds(startIdea, -1);

        SDRIdea goalSDRIdea = sdrIdeaSerializer.serialize(goalIdeas[i]);
        SDRIdea planSDRIdea = sdrIdeaSerializer.serialize(startIdeaPlan);
        SDRIdea targetSDRIdea  = sdrIdeaSerializer.serialize(startIdeaPlan);

        //removeLastSDR(targetSDRIdea);

        int[][][][] input = new int[1][][][];
        input[0] = goalSDRIdea.getSdr();

        int[][][][] target = new int[1][][][];
        target[0] = targetSDRIdea.getSdr();

        int[][][][] output = new int[1][][][];
        output[0] = planSDRIdea.getSdr();

        dataSamples.add(new TransformerPlanDataSample(input, target, output));

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

  public static void removeLastSDR(SDRIdea targetSDRIdea) {
    int[][][] sdr = targetSDRIdea.getSdr();

    for (int i = sdr.length - 1; i >= 0; i--) {
      if(sumSDR(sdr[i]) > 0) {
        setSDRToZero(sdr[i]);
        return;
      }
    }
  }

  private static void setSDRToZero(int[][] sdr) {
    for(int j = 0; j < sdr.length; j++) {
      for(int k = 0; k < sdr[j].length; k++) {
          sdr[j][k] = 0;
      }
    }
  }

  public static int sumSDR(int[][] sdr) {
    int sum = 0;

    for (int j = 0; j < sdr.length; j++) {
      for (int k = 0; k < sdr[j].length; k++) {
        sum += sdr[j][k];
      }
    }
    return sum;
  }

  @NotNull
  private static List<TransformerPlanDataSample> saveDataSamplesInFile(int j, Gson gson, List<TransformerPlanDataSample> dataSamples) throws IOException {
    System.out.println("Saving dataPlanSDR_" + j + ".json!");
    String json = gson.toJson(dataSamples);

    FileWriter fileWriter = new FileWriter(
            "/opt/repository/dataPlanSDR/dataPlanSDR_" + j + ".json");
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    bufferedWriter.write(json);
    bufferedWriter.close();

    System.out.println("dataPlanSDR_" + j + ".json Saved!");

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
