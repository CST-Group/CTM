package br.unicamp.ctm.representation.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import scala.Int;

public class SDRIdea {

  private int[][][] sdr;
  private int defaultValue;
  private int activeValue;

  public SDRIdea(int channels, int rows, int columns) {
    this.setSdr(new int[channels][rows][columns]);
    this.setDefaultValue(0);
    this.setActiveValue(1);
  }

  public int[][][] getSdr() {
    return sdr;
  }

  public void setSdr(int[][][] sdr) {
    this.sdr = sdr;
  }

  public int getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(int defaultValue) {
    this.defaultValue = defaultValue;
  }







  public int getActiveValue() {
    return activeValue;
  }

  public void setActiveValue(int activeValue) {
    this.activeValue = activeValue;
  }

}
