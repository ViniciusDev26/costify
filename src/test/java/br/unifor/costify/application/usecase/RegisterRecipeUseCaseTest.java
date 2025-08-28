package br.unifor.costify.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.command.RegisterRecipeCommand;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.errors.RecipeAlreadyExistsException;
import br.unifor.costify.application.factory.RecipeFactory;
import br.unifor.costify.application.validation.ValidationService;
import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.service.RecipeCostCalculationService;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.IngredientCost;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeCost;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RegisterRecipeUseCaseTest {
  @Mock private RecipeRepository recipeRepository;
  @Mock private IngredientRepository ingredientRepository;
  @Mock private IdGenerator idGenerator;
  @Mock private ValidationService validationService;
  @Mock private RecipeCostCalculationService costCalculationService;

  private RegisterRecipeUseCase useCase;
  private RecipeFactory recipeFactory;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    recipeFactory = new RecipeFactory(idGenerator);
    useCase = new RegisterRecipeUseCase(recipeRepository, ingredientRepository, recipeFactory, validationService, costCalculationService);
  }

  @Test
  void execute_withValidCommand_shouldSaveAndReturnRecipeDto() {
    Id ingredientId1 = Id.of("ingredient-1");
    Id ingredientId2 = Id.of("ingredient-2");
    RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredientId1, 0.5, Unit.KG);
    RecipeIngredient recipeIngredient2 = new RecipeIngredient(ingredientId2, 0.2, Unit.KG);
    List<RecipeIngredient> ingredients = List.of(recipeIngredient1, recipeIngredient2);

    RegisterRecipeCommand command = new RegisterRecipeCommand("Bread Recipe", ingredients);

    // Mock ingredients
    Ingredient ingredient1 = new Ingredient(ingredientId1, "Flour", 1000.0, Money.of(5.0), Unit.G);
    Ingredient ingredient2 = new Ingredient(ingredientId2, "Sugar", 1000.0, Money.of(3.0), Unit.G);
    
    when(recipeRepository.existsByName("Bread Recipe")).thenReturn(false);
    when(idGenerator.generate()).thenReturn("test-recipe-id-123");
    when(ingredientRepository.findById(ingredientId1)).thenReturn(Optional.of(ingredient1));
    when(ingredientRepository.findById(ingredientId2)).thenReturn(Optional.of(ingredient2));
    
    // Mock cost calculation
    IngredientCost ingredientCost1 = new IngredientCost(ingredientId1, "Flour", 0.5, Unit.KG, Money.of(2.5));
    IngredientCost ingredientCost2 = new IngredientCost(ingredientId2, "Sugar", 0.2, Unit.KG, Money.of(0.6));
    RecipeCost recipeCost = new RecipeCost(Id.of("test-recipe-id-123"), "Bread Recipe", List.of(ingredientCost1, ingredientCost2));
    when(costCalculationService.calculateCost(any(Recipe.class), any(Map.class))).thenReturn(recipeCost);

    Recipe savedRecipe = new Recipe(Id.of("test-recipe-id-123"), "Bread Recipe", ingredients, Money.of(3.1));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

    RecipeDto result = useCase.execute(command);

    assert result.name().equals("Bread Recipe");
    assert result.ingredients().size() == 2;
    assert result.ingredients().contains(recipeIngredient1);
    assert result.ingredients().contains(recipeIngredient2);

    verify(recipeRepository).existsByName("Bread Recipe");
    verify(ingredientRepository).findById(ingredientId1);
    verify(ingredientRepository).findById(ingredientId2);
    verify(costCalculationService).calculateCost(any(Recipe.class), any(Map.class));
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

    // Mock ingredient
    Ingredient ingredient = new Ingredient(ingredientId, "Flour", 1000.0, Money.of(5.0), Unit.G);
    
    when(recipeRepository.existsByName("Cake Recipe")).thenReturn(false);
    when(idGenerator.generate()).thenReturn("cake-recipe-id-456");
    when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));
    
    // Mock cost calculation
    IngredientCost ingredientCost = new IngredientCost(ingredientId, "Flour", 1.0, Unit.KG, Money.of(5.0));
    RecipeCost recipeCost = new RecipeCost(Id.of("cake-recipe-id-456"), "Cake Recipe", List.of(ingredientCost));
    when(costCalculationService.calculateCost(any(Recipe.class), any(Map.class))).thenReturn(recipeCost);

    Recipe savedRecipe = new Recipe(Id.of("cake-recipe-id-456"), "Cake Recipe", ingredients, Money.of(5.0));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

    useCase.execute(command);

    verify(recipeRepository, times(1)).existsByName("Cake Recipe");
    verify(ingredientRepository, times(1)).findById(ingredientId);
    verify(costCalculationService, times(1)).calculateCost(any(Recipe.class), any(Map.class));
    verify(recipeRepository, times(1)).save(any(Recipe.class));
  }
}
