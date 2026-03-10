package br.unifor.costify.catalog.application.dto.response;

import br.unifor.costify.shared.domain.valueobject.Unit;

public record UnitDto(String name, String type, double factorToBase) {
  public static UnitDto fromDomain(Unit unit) {
    return new UnitDto(unit.name(), unit.getType().name(), unit.toBase(1.0));
  }
}
