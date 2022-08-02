package br.unicamp.ctm.representation.idea.model;

public class DataSample<T> {
  private T[][][] x;
  private T[][] y;

  public DataSample(T[][][] x, T[][] y) {
    this.x = x;
    this.y = y;
  }
}
