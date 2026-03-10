package br.unifor.costify.infra.controllers;

import br.unifor.costify.application.dto.command.RegisterRecipeCommand;
import br.unifor.costify.application.dto.command.UpdateRecipeCommand;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.usecase.GetRecipeByIdUseCase;
import br.unifor.costify.application.usecase.ListRecipesUseCase;
import br.unifor.costify.application.usecase.RegisterRecipeUseCase;
import br.unifor.costify.application.usecase.UpdateRecipeUseCase;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.infra.controllers.dto.RecipeControllerRegisterRequest;
import br.unifor.costify.infra.controllers.dto.RecipeControllerRegisterIngredientDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
  private final RegisterRecipeUseCase registerRecipeUseCase;
  private final ListRecipesUseCase listRecipesUseCase;
  private final GetRecipeByIdUseCase getRecipeByIdUseCase;
  private final UpdateRecipeUseCase updateRecipeUseCase;

  public RecipeController(
      RegisterRecipeUseCase registerRecipeUseCase,
      ListRecipesUseCase listRecipesUseCase,
      GetRecipeByIdUseCase getRecipeByIdUseCase,
      UpdateRecipeUseCase updateRecipeUseCase) {
    this.registerRecipeUseCase = registerRecipeUseCase;
    this.listRecipesUseCase = listRecipesUseCase;
    this.getRecipeByIdUseCase = getRecipeByIdUseCase;
    this.updateRecipeUseCase = updateRecipeUseCase;
  }

  @GetMapping
  public List<RecipeDto> listRecipes() {
    return listRecipesUseCase.execute();
  }

  @GetMapping("/{id}")
  public RecipeDto getRecipeById(@PathVariable String id) {
    return getRecipeByIdUseCase.execute(id);
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

  @PutMapping("/{id}")
  public RecipeDto updateRecipe(
      @PathVariable String id,
      @RequestBody @Valid RecipeControllerRegisterRequest request) {
    List<RecipeIngredient> ingredients = request.ingredients().stream()
        .map(dto -> new RecipeIngredient(
            Id.of(dto.ingredientId()),
            dto.quantity(),
            dto.unit()))
        .toList();

    UpdateRecipeCommand command =
        new UpdateRecipeCommand(request.name(), ingredients);
    return updateRecipeUseCase.execute(Id.of(id), command);
  }
}