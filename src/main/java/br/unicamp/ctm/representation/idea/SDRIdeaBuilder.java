package br.unicamp.ctm.representation.idea;

import br.unicamp.ctm.representation.model.SDRIdea;

public class SDRIdeaBuilder {
  public SDRIdea build(int channels, int rows, int columns, int defaultValue, int activeValue) {
    SDRIdea sdrIdea = new SDRIdea(channels, rows, columns);

    initializeMatrix(sdrIdea, channels, rows, columns, defaultValue);

    sdrIdea.setDefaultValue(defaultValue);
    sdrIdea.setActiveValue(activeValue);

    return sdrIdea;
  }

  private int[][][] initializeMatrix(SDRIdea sdrIdea, int channels, int rows, int columns, int defaultValue) {

    for (int k = 0; k < channels; k++) {
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
          sdrIdea.getSdr()[k][i][j] = defaultValue;
        }
      }
    }

    return sdrIdea.getSdr();
  }

}
