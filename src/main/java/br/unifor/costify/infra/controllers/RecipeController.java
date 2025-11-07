package br.unifor.costify.infra.controllers;

import br.unifor.costify.application.dto.command.RegisterRecipeCommand;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.usecase.ListRecipesUseCase;
import br.unifor.costify.application.usecase.RegisterRecipeUseCase;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.infra.controllers.dto.RecipeControllerRegisterRequest;
import br.unifor.costify.infra.controllers.dto.RecipeControllerRegisterIngredientDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
  private final RegisterRecipeUseCase registerRecipeUseCase;
  private final ListRecipesUseCase listRecipesUseCase;

  public RecipeController(
      RegisterRecipeUseCase registerRecipeUseCase, ListRecipesUseCase listRecipesUseCase) {
    this.registerRecipeUseCase = registerRecipeUseCase;
    this.listRecipesUseCase = listRecipesUseCase;
  }

  @GetMapping
  public List<RecipeDto> listRecipes() {
    return listRecipesUseCase.execute();
  }

  @PostMapping
  public RecipeDto registerRecipe(@RequestBody @Valid RecipeControllerRegisterRequest request) {
    List<RecipeIngredient> ingredients = request.ingredients().stream()
        .map(dto -> new RecipeIngredient(
            Id.of(dto.ingredientId()),
            dto.quantity(),
            dto.unit()))
        .toList();

    RegisterRecipeCommand command =
        new RegisterRecipeCommand(request.name(), ingredients);
    return registerRecipeUseCase.execute(command);
  }
}