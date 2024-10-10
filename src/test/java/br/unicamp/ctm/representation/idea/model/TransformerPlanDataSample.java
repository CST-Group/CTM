package br.unicamp.ctm.representation.idea.model;

public class TransformerPlanDataSample {

    private int[][][][] input;
    private int[][][][] target;
    private int[][][][] output;

    public TransformerPlanDataSample(int[][][][] input, int[][][][] target, int[][][][] output) {
        this.input = input;
        this.target = target;
        this.output = output;
    }
}
