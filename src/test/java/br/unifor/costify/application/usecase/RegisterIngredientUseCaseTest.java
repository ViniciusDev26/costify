package br.unifor.costify.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.dto.command.RegisterIngredientCommand;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.application.errors.IngredientAlreadyExistsException;
import br.unifor.costify.application.factory.IngredientFactory;
import br.unifor.costify.application.validation.ValidationService;
import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RegisterIngredientUseCaseTest {

  @Mock private IngredientRepository ingredientRepository;

  @Mock private IdGenerator idGenerator;

  @Mock private ValidationService validationService;

  private RegisterIngredientUseCase useCase;
  private IngredientFactory ingredientFactory;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ingredientFactory = new IngredientFactory(idGenerator);
    useCase = new RegisterIngredientUseCase(ingredientRepository, ingredientFactory, validationService);
  }

  @Test
  void execute_withValidCommand_shouldSaveAndReturnIngredientDto() {
    RegisterIngredientCommand command = new RegisterIngredientCommand("Flour", 1.0, 5.0, Unit.KG);

    when(ingredientRepository.existsByName("Flour")).thenReturn(false);
    when(idGenerator.generate()).thenReturn("test-id-123");

    Ingredient savedIngredient = new Ingredient(idGenerator, "Flour", 1.0, Money.of(5.0), Unit.KG);
    when(ingredientRepository.save(any(Ingredient.class))).thenReturn(savedIngredient);

    IngredientDto result = useCase.execute(command);

    assert result.name().equals("Flour");
    assert result.packageQuantity() == 1.0;
    assert result.packagePrice() == 5.0;
    assert result.packageUnit() == Unit.KG;

    verify(ingredientRepository).existsByName("Flour");
    verify(ingredientRepository).save(any(Ingredient.class));
  }

  @Test
  void execute_withExistingIngredientName_shouldThrowException() {
    RegisterIngredientCommand command = new RegisterIngredientCommand("Flour", 1.0, 5.0, Unit.KG);

    when(ingredientRepository.existsByName("Flour")).thenReturn(true);

    try {
      useCase.execute(command);
      assert false;
    } catch (IngredientAlreadyExistsException e) {
      assert e.getMessage().contains("Flour");
      assert e.getMessage().contains("already exists");
    }

    verify(ingredientRepository).existsByName("Flour");
    verify(ingredientRepository, never()).save(any(Ingredient.class));
  }

  @Test
  void execute_withValidCommand_shouldCallRepositoryMethods() {
    RegisterIngredientCommand command = new RegisterIngredientCommand("Sugar", 2.0, 8.0, Unit.KG);

    when(ingredientRepository.existsByName("Sugar")).thenReturn(false);
    when(idGenerator.generate()).thenReturn("sugar-id-456");

    Ingredient savedIngredient = new Ingredient(idGenerator, "Sugar", 2.0, Money.of(8.0), Unit.KG);
    when(ingredientRepository.save(any(Ingredient.class))).thenReturn(savedIngredient);

    useCase.execute(command);

    verify(ingredientRepository, times(1)).existsByName("Sugar");
    verify(ingredientRepository, times(1)).save(any(Ingredient.class));
  }
}
