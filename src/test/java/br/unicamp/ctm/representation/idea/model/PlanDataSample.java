package br.unicamp.ctm.representation.idea.model;

public class PlanDataSample {

    private int[][][][] goal;
    private int[] control;

    public PlanDataSample(int[][][][] x, int[] control) {
        this.goal = x;
        this.control = control;
    }
}
