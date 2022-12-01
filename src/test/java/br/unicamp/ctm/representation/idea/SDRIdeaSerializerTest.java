package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.model.SDRIdea;
import com.google.gson.Gson;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class SDRIdeaSerializerTest {

  private SDRIdeaSerializer sdrIdeaSerializer;
  private SDRIdeaDeserializer sdrIdeaDeserializer;

  @Before
  public void setup() {

    sdrIdeaSerializer = new SDRIdeaSerializer(10, 32, 32);
    sdrIdeaDeserializer = new SDRIdeaDeserializer(sdrIdeaSerializer.getDictionary(), sdrIdeaSerializer.getValues());
  }

  private Idea initialize() {
    Idea idea = new Idea("Rock Music", "Hey ho let's go!", 0);

    idea.add(new Idea("Metallica", "Black Album", 0)).add(new Idea("Unforgiven", 3.14, 1))
        .add(new Idea("Enter Sadman", "Seek and destroy"));
    idea.add(new Idea("Foo Fighters", "The sky's the neighborhood", 0))
        .add(new Idea("Pretender", new long[]{256}));
    idea.add(new Idea("Black Sabbath", Arrays.asList(3.4, 2.221, 0.23), 1))
        .add(new Idea("Paranoid", new short[]{34, 18, 10}));
    idea.add(new Idea("Gun's in Roses", "Sweet child o' mine", 2))
        .add(new Idea("November Rain", new float[]{-18f, 1.2f, 2f, 5.2f, -1f, 0f, 1000f}));

    return idea;
  }

  @Test
  public void testDeserializer() throws Exception {
    Idea idea = initialize();

    SDRIdea sdrIdea = sdrIdeaSerializer.serialize(idea);
    Idea convertedIdea = sdrIdeaDeserializer.deserialize(sdrIdea);

    System.out.println(convertedIdea.toString());
  }

}
