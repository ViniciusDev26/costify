package br.unifor.costify.infra.controllers;

import br.unifor.costify.application.dto.command.RegisterIngredientCommand;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.application.usecase.ListIngredientsUseCase;
import br.unifor.costify.application.usecase.RegisterIngredientUseCase;
import br.unifor.costify.infra.controllers.dto.IngredientControllerRegisterRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
  private final RegisterIngredientUseCase registerIngredientUseCase;
  private final ListIngredientsUseCase listIngredientsUseCase;

  public IngredientController(
      RegisterIngredientUseCase registerIngredientUseCase,
      ListIngredientsUseCase listIngredientsUseCase) {
    this.registerIngredientUseCase = registerIngredientUseCase;
    this.listIngredientsUseCase = listIngredientsUseCase;
  }

  @GetMapping
  public List<IngredientDto> listIngredients() {
    return listIngredientsUseCase.execute();
  }

  @PostMapping
  public IngredientDto registerIngredient(@RequestBody @Valid IngredientControllerRegisterRequest request) {
    RegisterIngredientCommand command =
        new RegisterIngredientCommand(
            request.name(),
            request.packageQuantity(),
            request.packagePrice(),
            request.packageUnit());
    return registerIngredientUseCase.execute(command);
  }
}
