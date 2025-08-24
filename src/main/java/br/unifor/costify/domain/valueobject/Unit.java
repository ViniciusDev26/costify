package br.unifor.costify.domain.valueobject;

public enum Unit {
  ML(1.0, Type.VOLUME),
  L(1000.0, Type.VOLUME),   // 1L = 1000ml
  G(1.0, Type.WEIGHT),
  KG(1000.0, Type.WEIGHT),  // 1kg = 1000g
  UN(1.0, Type.UNIT);

  private final double factorToBase;
  private final Type type;

  Unit(double factorToBase, Type type) {
    this.factorToBase = factorToBase;
    this.type = type;
  }

  public double toBase(double quantity) {
    return quantity * factorToBase;
  }

  public Type getType() {
    return type;
  }

  public enum Type {
    VOLUME,
    WEIGHT,
    UNIT
  }
}
