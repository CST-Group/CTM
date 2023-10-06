package br.unicamp.ctm.representation.idea.model;

public class SDRDataSample {

  private int[][][][] goal;

  private int[][][][] target;

  private int[][][] output;


  //private int[] xc;

  /*public SDRDataSample(int[][][][] x, int[][][] y, int[] xc) {
    this.x = x;
    this.y = y;
    this.xc = xc;
  }*/

  public SDRDataSample(int[][][][] goal, int[][][][] target, int[][][] output) {
    this.goal = goal;
    this.target = target;
    this.output = output;
//    this.xc = xc;
  }

}
