package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.model.SDRIdeaArray;

public class SDRIdeaArrayBuilder {

    public SDRIdeaArray build(int totalOfIdeas, int totalOfValues, int defaultValue, int startWord) {
        SDRIdeaArray sdrIdeaArray = new SDRIdeaArray(totalOfIdeas, totalOfValues, defaultValue);

        initializeMatrix(sdrIdeaArray, startWord);

        return sdrIdeaArray;
    }

    private int[] initializeMatrix(SDRIdeaArray sdrIdeaArray, int startWord) {

        sdrIdeaArray.getSdr()[0] = startWord;
        for (int k = 1; k < sdrIdeaArray.getSdr().length; k++) {
            sdrIdeaArray.getSdr()[k] = sdrIdeaArray.getDefaultValue();
        }

        return sdrIdeaArray.getSdr();
    }
}
