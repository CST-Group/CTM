package br.unicamp.ctm.representation.model;

public class SDRIdeaArray {

    private int[] sdr;
    private int defaultValue = 0;

    public SDRIdeaArray(int totalIdeas) {
        this.setSdr(new int[totalIdeas*72]);
    }

    public SDRIdeaArray(int totalOfIdeas, int totalOfValues, int defaultValue) {
        this.setSdr(new int[15 + 2 + totalOfValues*6 + ((totalOfIdeas - 1) * (21 + totalOfValues*6))]);
        this.setDefaultValue(defaultValue);

    }

    public int[] getSdr() {
        return sdr;
    }

    public void setSdr(int[] sdr) {
        this.sdr = sdr;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }
}
