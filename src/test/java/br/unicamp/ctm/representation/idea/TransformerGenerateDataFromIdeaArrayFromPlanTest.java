package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.idea.model.SDRDataSample;
import br.unicamp.ctm.representation.idea.model.TransformerPlanArrayDataSample;
import br.unicamp.ctm.representation.idea.model.TransformerPlanDataSample;
import br.unicamp.ctm.representation.model.ArrayDictionary;
import br.unicamp.ctm.representation.model.Dictionary;
import br.unicamp.ctm.representation.model.SDRIdea;
import br.unicamp.ctm.representation.model.SDRIdeaArray;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;

public class TransformerGenerateDataFromIdeaArrayFromPlanTest {


  private static ArrayDictionary dictionary;
  private static SDRIdeaArraySerializer sdrIdeaArraySerializer;

  private static SDRIdeaArrayDeserializer sdrIdeaArrayDeserializer;

  @Before
  public void setup() throws FileNotFoundException {
    Gson gson = new Gson();

    File dictionaryFile = new File("/opt/repository/dataTrainingShortSDR/dictionary.json");

    if(dictionaryFile.exists())
      dictionary = gson.fromJson(new FileReader(dictionaryFile), ArrayDictionary.class);
    else {
      Map<Integer, Object> dictionaryMap = new HashMap<>();
      dictionaryMap.put(0, "<pad>");
      dictionaryMap.put(1, "<sos>");
      dictionaryMap.put(2, "<eos>");
      dictionaryMap.put(3, "<unk>");
      dictionaryMap.put(4, "+");
      dictionaryMap.put(5, "-");
      dictionaryMap.put(6, 0);
      dictionaryMap.put(7, 1);
      dictionaryMap.put(8, 2);
      dictionaryMap.put(9, 3);
      dictionaryMap.put(10, 4);
      dictionaryMap.put(11, 5);
      dictionaryMap.put(12, 6);
      dictionaryMap.put(13, 7);
      dictionaryMap.put(14, 8);
      dictionaryMap.put(15, 9);

      dictionary = new ArrayDictionary(dictionaryMap);
    }


    sdrIdeaArraySerializer = new SDRIdeaArraySerializer(5, 7, 0, dictionary);
    sdrIdeaArrayDeserializer = new SDRIdeaArrayDeserializer(sdrIdeaArraySerializer.getDictionary());
  }

  public static void main(String[] args) throws Exception {

    Gson gson = new Gson();

    //File dictionaryFile = new File("/opt/repository/dataPlanSDR/dictionary.json");

//    if(dictionaryFile.exists())
//      dictionary = gson.fromJson(new FileReader(dictionaryFile), ArrayDictionary.class);
//    else {
      Map<Integer, Object> dictionaryMap = new HashMap<>();
      dictionaryMap.put(0, "<pad>");
      dictionaryMap.put(1, "<sos>");
      dictionaryMap.put(2, "<eos>");
      dictionaryMap.put(3, "<unk>");
      dictionaryMap.put(4, "+");
      dictionaryMap.put(5, "-");
      dictionaryMap.put(6, 0);
      dictionaryMap.put(7, 1);
      dictionaryMap.put(8, 2);
      dictionaryMap.put(9, 3);
      dictionaryMap.put(10, 4);
      dictionaryMap.put(11, 5);
      dictionaryMap.put(12, 6);
      dictionaryMap.put(13, 7);
      dictionaryMap.put(14, 8);
      dictionaryMap.put(15, 9);
      dictionaryMap.put(16,"goal");
      dictionaryMap.put(17,"1");
      dictionaryMap.put(18,"7");
      dictionaryMap.put(19,"");
      dictionaryMap.put(20,"initialNode");
      dictionaryMap.put(21,"3");
      dictionaryMap.put(22,"goalAction");
      dictionaryMap.put(23,"goalTag");
      dictionaryMap.put(24,"goalSlot");
      dictionaryMap.put(25,"occupiedNodes");
      dictionaryMap.put(26,"9");
      dictionaryMap.put(27,"moveToNode");
      dictionaryMap.put(28,"moveTo");
      dictionaryMap.put(29,"pick");
      dictionaryMap.put(30,"stop");
      dictionaryMap.put(31,"initialTag");
      dictionaryMap.put(32,"place");

      dictionary = new ArrayDictionary(dictionaryMap);
    //}

    sdrIdeaArraySerializer = new SDRIdeaArraySerializer(6, 7, 0, dictionary);

    testNewGenerateDataFile();

    System.out.println("Saving dictionary.json!");

    FileWriter fileWriter = new FileWriter("/opt/repository/dataPlanSDR/dictionary.json");
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    bufferedWriter.write(gson.toJson(sdrIdeaArraySerializer.getDictionary()));
    bufferedWriter.close();

    System.out.println("dictionary.json Saved!");

  }

  public static void testNewGenerateDataFile() throws Exception {
    int j = 0;

    Gson gson = new Gson();
    List<TransformerPlanArrayDataSample> dataSamples = new ArrayList<>();

    for (int k = 0; k < 2499; k++) {

      File planFile = new File("/opt/repository/dataTrainingIdea/plan/planIdeaFile"+k+".json");
      Idea[] planIdeas = gson.fromJson(new FileReader(planFile), Idea[].class);

      File goalFile = new File("/opt/repository/dataTrainingIdea/goal/goalIdeaFile"+k+".json");
      Idea[] goalIdeas = gson.fromJson(new FileReader(goalFile), Idea[].class);

      for (int i = 0; i < goalIdeas.length; i++) {
        SDRIdeaArray goalSDRIdea = sdrIdeaArraySerializer.serialize(goalIdeas[i]);
        SDRIdeaArray planSDRIdea = sdrIdeaArraySerializer.serialize(planIdeas[i]);
//        SDRIdeaArray targetSDRIdea  = sdrIdeaArraySerializer.serialize(startIdeaPlan);

        int[] input = goalSDRIdea.getSdr();
        int[] target = Arrays.stream(Arrays.copyOfRange(planSDRIdea.getSdr(), 0, planSDRIdea.getSdr().length - 1))
                .map(e -> e == 2 ? 0 : e)
                .toArray();
        int[] output = planSDRIdea.getSdr();
//                Arrays.copyOfRange(planSDRIdea.getSdr(), 0, planSDRIdea.getSdr().length);

        dataSamples.add(new TransformerPlanArrayDataSample(input, target, output));

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
  private static List<TransformerPlanArrayDataSample> saveDataSamplesInFile(int j, Gson gson, List<TransformerPlanArrayDataSample> dataSamples) throws IOException {
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
}
