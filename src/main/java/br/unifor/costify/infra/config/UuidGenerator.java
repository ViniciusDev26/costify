package br.unifor.costify.infra.config;

import br.unifor.costify.domain.contracts.IdGenerator;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidGenerator implements IdGenerator {
  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
