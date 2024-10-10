package br.unicamp.ctm.representation.idea.model;

public class SDRDataSample {

  private int[][][][] goal;

  //private int[][][][] target;

  private int[][][] output;


  private int[] control;

  public SDRDataSample(int[][][][] x, int[][][] y, int[] control) {
    this.goal = x;
    this.output = y;
    this.control = control;
  }

  /*public SDRDataSample(int[][][][] goal, int[][][][] target, int[][][] output) {
    this.goal = goal;
    this.target = target;
    this.output = output;
//    this.xc = xc;
  }*/

}
