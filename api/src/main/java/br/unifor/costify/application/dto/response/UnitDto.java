package br.unifor.costify.application.dto.response;

import br.unifor.costify.domain.valueobject.Unit;

public record UnitDto(String name, String type, double factorToBase) {
  public static UnitDto fromDomain(Unit unit) {
    return new UnitDto(unit.name(), unit.getType().name(), unit.toBase(1.0));
  }
}
