package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.model.MatrixIdea;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class MatrixIdeaSerializerTest {

  private MatrixIdeaSerializer matrixIdeaSerializer;

  @Before
  public void setup() {
    matrixIdeaSerializer = new MatrixIdeaSerializer(10, 10, 3);
  }

  private Idea initialize() {
    Idea idea = new Idea("Rock Music", "Hey ho let's go!", 0);

    idea.add(new Idea("Metallica", "Black Album", 0)).add(new Idea("Unforgiven", 3.14, 1))
        .add(new Idea("Enter Sadman", "Seek and destroy"));
    idea.add(new Idea("Foo Fighters", "The sky's the neighborhood", 0))
        .add(new Idea("Pretender", new long[]{256}));
    idea.add(new Idea("Black Sabbath", new double[]{3.41, 2.22, 1.23}, 1))
        .add(new Idea("Paranoid", new short[]{34, 18, 10}));
    idea.add(new Idea("Gun's in Roses", "Sweet child o' mine", 2))
        .add(new Idea("November Rain", new float[]{1.2f, 2f, 8f}));

    return idea;
  }

  @Test
  public void testSerializer() {
    Idea idea = initialize();

    try {

      Map<String, Double> dictionary = new HashMap<>();
      dictionary.put("Rock Music", 2d);
      dictionary.put("Paranoid", 4d);
      dictionary.put("Hey ho let's go!", 8d);
      dictionary.put("The sky's the neighborhood", 16d);
      dictionary.put("Killing in the name", 32d);
      dictionary.put("Seek and destroy", 64d);
      dictionary.put("Sweet child o' mine", 128d);
      dictionary.put("Metallica", 256d);
      dictionary.put("Unforgiven", 512d);
      dictionary.put("Enter Sadman", 1024d);
      dictionary.put("Foo Fighters", 2048d);
      dictionary.put("Pretender", 4096d);
      dictionary.put("Black Sabbath", 8192d);
      dictionary.put("Gun's in Roses", 16384d);
      dictionary.put("November Rain", 32768d);
      dictionary.put("Black Album", 65536d);

      MatrixIdea matrixIdea = matrixIdeaSerializer.serialize(idea, dictionary, false);

      MatrixIdeaDeserializer matrixIdeaDeserializer = new MatrixIdeaDeserializer();
      Idea convertedIdea = matrixIdeaDeserializer.deserialize(matrixIdea);

      IdeaComparator ideaComparator = new IdeaComparator();

      assert ideaComparator.compare(idea, convertedIdea) == 1;

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
