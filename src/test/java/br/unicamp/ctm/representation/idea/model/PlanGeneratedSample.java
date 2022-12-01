package br.unicamp.ctm.representation.idea.model;

public class PlanGeneratedSample {
  private int[][][] realPlan;
  private int[][][] fakePlan;

  public PlanGeneratedSample(int[][][] realPlan, int[][][] fakePlan) {
    this.realPlan = realPlan;
    this.fakePlan = fakePlan;
  }

  public int[][][] getRealPlan() {
    return realPlan;
  }

  public void setRealPlan(int[][][] realPlan) {
    this.realPlan = realPlan;
  }

  public int[][][] getFakePlan() {
    return fakePlan;
  }

  public void setFakePlan(int[][][] fakePlan) {
    this.fakePlan = fakePlan;
  }
}
