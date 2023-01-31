package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.idea.model.DataSample;
import br.unicamp.ctm.representation.idea.model.SDRDataSample;
import br.unicamp.ctm.representation.model.MatrixIdea;
import br.unicamp.ctm.representation.model.SDRIdea;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class GenerateDataFromIdeaSDRTest {


  private Map<String, int[]> dictionary;
  private Map<Integer, int[]> values;
  private SDRIdeaSerializer sdrIdeaSerializer;

  private SDRIdeaDeserializer sdrIdeaDeserializer;

  @Before
  public void setup() throws FileNotFoundException {
    Gson gson = new Gson();

    File dictionaryFile = new File("/opt/repository/dataTrainingShortSDR/dictionary.json");
    File valuesFile = new File("/opt/repository/dataTrainingShortSDR/values.json");

    dictionary = new HashMap<String, int[]>();
    values = new HashMap<Integer, int[]>();

    Type type = new TypeToken<Map<String, int[]>>(){}.getType();

    if(dictionaryFile.exists())
      dictionary = gson.fromJson(new FileReader(dictionaryFile), type);

    type = new TypeToken<Map<Integer, int[]>>(){}.getType();

    if(valuesFile.exists())
      values = gson.fromJson(new FileReader(valuesFile), type);

    sdrIdeaSerializer = new SDRIdeaSerializer(16,32,32);
    sdrIdeaSerializer.setDictionary(dictionary);
    sdrIdeaSerializer.setValues(values);

    sdrIdeaDeserializer = new SDRIdeaDeserializer(dictionary, values);
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

        dataSamples.add(new SDRDataSample(x, planSDRIdea.getSdr()));

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

    System.out.println("Saving values.json!");

    fileWriter = new FileWriter("/opt/repository/dataTrainingShortSDR/values.json");
    bufferedWriter = new BufferedWriter(fileWriter);

    bufferedWriter.write(gson.toJson(sdrIdeaSerializer.getValues()));
    bufferedWriter.close();

    System.out.println("values.json Saved!");
  }

  private Integer resetIdeaIds(Idea idea, Integer value) {
    value = value + 1;
    idea.setId(value);

    for (Idea childIdea : idea.getL()) {
      value = resetIdeaIds(childIdea, value);
    }

    return value;
  }

  @Test
  public void test() {

    double value = -7d;

//    String stringValue = Long.toBinaryString(Double.doubleToRawLongBits(value));
    String stringValue = "1100000000110010000000000000000000000000000000000000000000000000";

    int fillCount = 32 * 2 - stringValue.length();

    if (fillCount > 0) {
      if (value < 0d) {
        fillCount--;
        stringValue = "1" + StringUtils.repeat("0", fillCount) + stringValue;
      } else {
        stringValue = StringUtils.repeat("0", fillCount) + stringValue;
      }
    }

    double d = Double.longBitsToDouble(Long.parseUnsignedLong(stringValue, 2));


    System.out.println(d);
  }

}
