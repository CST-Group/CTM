package br.unicamp.ctm.memory.grpc;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.ctm.memory.DistributedMemory;
import br.unicamp.ctm.memory.DistributedMemoryType;

public class GRPCDistributedMemory implements Memory, DistributedMemory {

  private DistributedMemoryType type;
  private Object I;
  private Double eval;

  @Override
  public Object getI() {
    return null;
  }

  @Override
  public int setI(Object info) {
    return 0;
  }

  @Override
  public Double getEvaluation() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public void setEvaluation(Double eval) {

  }

  @Override
  public Long getTimestamp() {
    return null;
  }

  @Override
  public DistributedMemoryType getType() {
    return null;
  }
}
