package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.idea.model.PlanGeneratedSample;
import br.unicamp.ctm.representation.model.SDRIdea;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class PlanGeneratedTest {

  private SDRIdeaDeserializer sdrIdeaDeserializer;
  private Map<String, int[]> dictionary;
  private Map<Integer, int[]> values;
  private Gson gson;

  @Before
  public void setup() throws FileNotFoundException {

    File valuesFile = new File("./src/test/resources/values.json");
    File dictionaryFile  = new File("./src/test/resources/dictionary.json");

    gson = new Gson();

    Type typeValues = new TypeToken<Map<Integer, int[]>>(){}.getType();
    Type typeDictionary = new TypeToken<Map<String, int[]>>(){}.getType();

    dictionary = gson.fromJson(new FileReader(dictionaryFile), typeDictionary);
    values = gson.fromJson(new FileReader(valuesFile), typeValues);

    sdrIdeaDeserializer = new SDRIdeaDeserializer(dictionary, values);
  }

  @Test
  public void planGeneratedTest()
      throws FileNotFoundException {

    File planGeneratedFile  = new File("./src/test/resources/pix2pix_plan_generated_dic_9.json");
    PlanGeneratedSample planGeneratedSample = gson.fromJson(new FileReader(planGeneratedFile), PlanGeneratedSample.class);

    SDRIdea generatedPlanSDRIdea = new SDRIdea(16, 32, 32);
    generatedPlanSDRIdea.setSdr(planGeneratedSample.getFakePlan());

    SDRIdea originalPlanSDRIdea = new SDRIdea(16, 32, 32);
    originalPlanSDRIdea.setSdr(planGeneratedSample.getRealPlan());

    Idea originalPlanIdea = sdrIdeaDeserializer.deserialize(originalPlanSDRIdea);
    Idea generatedPlanIdea = sdrIdeaDeserializer.deserialize(generatedPlanSDRIdea);

    System.out.print("exit");
  }

}
