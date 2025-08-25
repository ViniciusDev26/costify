package br.unifor.costify.infra.controllers;

import br.unifor.costify.application.dto.command.RegisterIngredientCommand;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.application.usecase.RegisterIngredientUseCase;
import br.unifor.costify.infra.controllers.dto.IngredientControllerRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
  private final RegisterIngredientUseCase registerIngredientUseCase;

  public IngredientController(RegisterIngredientUseCase registerIngredientUseCase) {
    this.registerIngredientUseCase = registerIngredientUseCase;
  }

  @PostMapping
  public IngredientDto registerIngredient(@RequestBody @Valid IngredientControllerRequest request) {
    RegisterIngredientCommand command =
        new RegisterIngredientCommand(
            request.name(),
            request.packageQuantity(),
            request.packagePrice(),
            request.packageUnit());
    return registerIngredientUseCase.execute(command);
  }
}
