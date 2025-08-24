package br.unifor.costify.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import br.unifor.costify.domain.contracts.IdGenerator;

public class IdTest {
  @Test
  void createIdFromValue_shouldReturnCorrectId() {
    Id id = Id.of("12345");
    assert id.getValue().equals("12345");
    assert id.toString() != null;
  }

  @Test
  void createIdFromNullValue_shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> Id.of(null));
    assertThrows(IllegalArgumentException.class, () -> Id.of(""));
    assertThrows(IllegalArgumentException.class, () -> Id.of("   "));
  }

  @Test
  void generateId_shouldCallGenerator() {
    // Mock do IdGenerator
    IdGenerator generator = () -> "generated-id";

    Id id = Id.generate(generator);
    assert id.getValue().equals("generated-id");
  }

  @Test
  void equalsAndHashCode_shouldWorkCorrectly() {
    Id id1 = Id.of("abc");
    Id id2 = Id.of("abc");
    Id id3 = Id.of("def");

    assert id1.equals(id2);
    assert !id1.equals(id3);
    assert id1.hashCode() == id2.hashCode();
    assert id1.hashCode() != id3.hashCode();
  }
}
