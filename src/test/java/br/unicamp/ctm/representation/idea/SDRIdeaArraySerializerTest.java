package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.model.ArrayDictionary;
import br.unicamp.ctm.representation.model.SDRIdea;
import br.unicamp.ctm.representation.model.SDRIdeaArray;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class SDRIdeaArraySerializerTest {

    private SDRIdeaArraySerializer sdrIdeaArraySerializer;
    private SDRIdeaArrayDeserializer sdrIdeaArrayDeserializer;

    @Before
    public void setup() {

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

        sdrIdeaArraySerializer = new SDRIdeaArraySerializer(10, 7, 0, new ArrayDictionary(dictionaryMap));
        sdrIdeaArrayDeserializer = new SDRIdeaArrayDeserializer(sdrIdeaArraySerializer.getDictionary());
        //sdrIdeaDeserializer = new SDRIdeaDeserializer(sdrIdeaSerializer.getDictionary(), 3, 4);
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
                .add(new Idea("November Rain", new float[]{-18f, 1.2f, -0.02f, 5.2f, -1f, 0f, 1000f}));

        return idea;
    }

    @Test
    public void testDeserializer() throws Exception {
        Idea idea = initialize();

        SDRIdeaArray sdrIdea = sdrIdeaArraySerializer.serialize(idea);
        System.out.println(sdrIdea.getSdr().length);

        Idea convertedIdea = sdrIdeaArrayDeserializer.deserialize(sdrIdea);
        System.out.println(convertedIdea.toString());
    }

}
