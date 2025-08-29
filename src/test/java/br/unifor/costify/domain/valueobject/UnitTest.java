package br.unifor.costify.domain.valueobject;

import org.junit.jupiter.api.Test;

public class UnitTest {
  @Test
  void conversionToBase_shouldBeCorrect() {
    assert Unit.ML.toBase(1) == 1.0;
    assert Unit.L.toBase(1) == 1000.0;
    assert Unit.TBSP.toBase(1) == 15.0;
    assert Unit.G.toBase(1) == 1.0;
    assert Unit.KG.toBase(1) == 1000.0;
    assert Unit.TBSP_BUTTER.toBase(1) == 14.0;
    assert Unit.UN.toBase(1) == 1.0;
  }

  @Test
  void unitType_shouldBeCorrect() {
    assert Unit.ML.getType() == Unit.Type.VOLUME;
    assert Unit.L.getType() == Unit.Type.VOLUME;
    assert Unit.TBSP.getType() == Unit.Type.VOLUME;
    assert Unit.G.getType() == Unit.Type.WEIGHT;
    assert Unit.KG.getType() == Unit.Type.WEIGHT;
    assert Unit.TBSP_BUTTER.getType() == Unit.Type.WEIGHT;
    assert Unit.UN.getType() == Unit.Type.UNIT;
  }

  @Test
  void tbspUnits_shouldHaveCorrectConversions() {
    // TBSP (liquid volume) = 15ml
    assert Unit.TBSP.toBase(1) == 15.0;
    assert Unit.TBSP.toBase(2) == 30.0;
    assert Unit.TBSP.toBase(0.5) == 7.5;
    
    // TBSP_BUTTER (solid fat weight) = 14g
    assert Unit.TBSP_BUTTER.toBase(1) == 14.0;
    assert Unit.TBSP_BUTTER.toBase(2) == 28.0;
    assert Unit.TBSP_BUTTER.toBase(0.5) == 7.0;
  }

  @Test
  void tbspUnits_shouldHaveCorrectTypes() {
    // TBSP is volume (for liquids like vanilla extract)
    assert Unit.TBSP.getType() == Unit.Type.VOLUME;
    
    // TBSP_BUTTER is weight (for solid fats like butter/margarine)
    assert Unit.TBSP_BUTTER.getType() == Unit.Type.WEIGHT;
  }

  @Test
  void volumeUnits_shouldAllConvertToMilliliters() {
    // All volume units should convert to milliliters as base
    assert Unit.ML.toBase(100) == 100.0;
    assert Unit.L.toBase(1) == 1000.0;
    assert Unit.TBSP.toBase(4) == 60.0; // 4 tbsp = 60ml
  }

  @Test
  void weightUnits_shouldAllConvertToGrams() {
    // All weight units should convert to grams as base
    assert Unit.G.toBase(100) == 100.0;
    assert Unit.KG.toBase(1) == 1000.0;
    assert Unit.TBSP_BUTTER.toBase(4) == 56.0; // 4 tbsp butter = 56g
  }
}
