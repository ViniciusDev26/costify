package br.unifor.costify.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.command.RegisterRecipeCommand;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.errors.RecipeAlreadyExistsException;
import br.unifor.costify.application.factory.RecipeFactory;
import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RegisterRecipeUseCaseTest {
  @Mock private RecipeRepository recipeRepository;
  @Mock private IdGenerator idGenerator;

  private RegisterRecipeUseCase useCase;
  private RecipeFactory recipeFactory;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    recipeFactory = new RecipeFactory(idGenerator);
    useCase = new RegisterRecipeUseCase(recipeRepository, recipeFactory);
  }

  @Test
  void execute_withValidCommand_shouldSaveAndReturnRecipeDto() {
    Id ingredientId1 = Id.of("ingredient-1");
    Id ingredientId2 = Id.of("ingredient-2");
    RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredientId1, 0.5, Unit.KG);
    RecipeIngredient recipeIngredient2 = new RecipeIngredient(ingredientId2, 0.2, Unit.KG);
    List<RecipeIngredient> ingredients = List.of(recipeIngredient1, recipeIngredient2);

    RegisterRecipeCommand command = new RegisterRecipeCommand("Bread Recipe", ingredients);

    when(recipeRepository.existsByName("Bread Recipe")).thenReturn(false);
    when(idGenerator.generate()).thenReturn("test-recipe-id-123");

    Recipe savedRecipe = new Recipe(idGenerator, "Bread Recipe", ingredients);
    when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

    RecipeDto result = useCase.execute(command);

    assert result.name().equals("Bread Recipe");
    assert result.ingredients().size() == 2;
    assert result.ingredients().contains(recipeIngredient1);
    assert result.ingredients().contains(recipeIngredient2);

    verify(recipeRepository).existsByName("Bread Recipe");
    verify(recipeRepository).save(any(Recipe.class));
  }

  @Test
  void execute_withExistingRecipeName_shouldThrowException() {
    Id ingredientId = Id.of("ingredient-1");
    RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 0.5, Unit.KG);
    List<RecipeIngredient> ingredients = List.of(recipeIngredient);

    RegisterRecipeCommand command = new RegisterRecipeCommand("Existing Recipe", ingredients);

    when(recipeRepository.existsByName("Existing Recipe")).thenReturn(true);

    try {
      useCase.execute(command);
      assert false;
    } catch (RecipeAlreadyExistsException e) {
      assert e.getMessage().contains("Existing Recipe");
      assert e.getMessage().contains("already exists");
    }

    verify(recipeRepository).existsByName("Existing Recipe");
    verify(recipeRepository, never()).save(any(Recipe.class));
  }

  @Test
  void execute_withValidCommand_shouldCallRepositoryMethods() {
    Id ingredientId = Id.of("ingredient-1");
    RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 1.0, Unit.KG);
    List<RecipeIngredient> ingredients = List.of(recipeIngredient);

    RegisterRecipeCommand command = new RegisterRecipeCommand("Cake Recipe", ingredients);

    when(recipeRepository.existsByName("Cake Recipe")).thenReturn(false);
    when(idGenerator.generate()).thenReturn("cake-recipe-id-456");

    Recipe savedRecipe = new Recipe(idGenerator, "Cake Recipe", ingredients);
    when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

    useCase.execute(command);

    verify(recipeRepository, times(1)).existsByName("Cake Recipe");
    verify(recipeRepository, times(1)).save(any(Recipe.class));
  }
}
