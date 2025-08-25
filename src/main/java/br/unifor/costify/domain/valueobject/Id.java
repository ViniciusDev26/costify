package br.unifor.costify.domain.valueobject;

import br.unifor.costify.domain.contracts.IdGenerator;
import java.util.Objects;

public final class Id {
  private final String value;

  private Id(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Id cannot be null or empty");
    }
    this.value = value;
  }

  public static Id of(String value) {
    return new Id(value);
  }

  public static Id generate(IdGenerator generator) {
    return new Id(generator.generate());
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Id)) return false;
    Id id = (Id) o;
    return value.equals(id.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
