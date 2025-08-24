package br.unifor.costify.domain.valueobject;

import org.junit.jupiter.api.Test;

public class UnitTest {
  @Test
  void conversionToBase_shouldBeCorrect() {
    assert Unit.ML.toBase(1) == 1.0;
    assert Unit.L.toBase(1) == 1000.0;
    assert Unit.G.toBase(1) == 1.0;
    assert Unit.KG.toBase(1) == 1000.0;
    assert Unit.UN.toBase(1) == 1.0;
  }

  @Test
  void unitType_shouldBeCorrect() {
    assert Unit.ML.getType() == Unit.Type.VOLUME;
    assert Unit.L.getType() == Unit.Type.VOLUME;
    assert Unit.G.getType() == Unit.Type.WEIGHT;
    assert Unit.KG.getType() == Unit.Type.WEIGHT;
    assert Unit.UN.getType() == Unit.Type.UNIT;
  }
}
