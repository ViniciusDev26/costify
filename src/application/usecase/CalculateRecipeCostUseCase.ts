import type { DecimalProvider } from '@domain/contracts/DecimalProvider.js'
import { RecipeCostCalculationService } from '@domain/services/RecipeCostCalculationService.js'
import { Id } from '@domain/valueobjects/Id.js'
import type { IngredientRepository } from '../contracts/IngredientRepository.js'
import type { RecipeRepository } from '../contracts/RecipeRepository.js'
import type { CalculateRecipeCostCommand } from '../dto/command/CalculateRecipeCostCommand.js'
import type { RecipeCostDto } from '../dto/response/RecipeCostDto.js'
import { IngredientNotFoundException } from '../errors/IngredientNotFoundException.js'
import { RecipeNotFoundException } from '../errors/RecipeNotFoundException.js'
import { RecipeMapper } from '../mapper/RecipeMapper.js'

export class CalculateRecipeCostUseCase {
  private readonly costCalculationService: RecipeCostCalculationService

  constructor(
    private readonly recipeRepository: RecipeRepository,
    private readonly ingredientRepository: IngredientRepository,
    private readonly decimalProvider: DecimalProvider
  ) {
    this.costCalculationService = new RecipeCostCalculationService(this.decimalProvider)
  }

  async execute(command: CalculateRecipeCostCommand): Promise<RecipeCostDto> {
    const recipeId = new Id(command.recipeId)

    // Find recipe
    const recipe = await this.recipeRepository.findById(recipeId)
    if (!recipe) {
      throw new RecipeNotFoundException(command.recipeId)
    }

    // Get all required ingredients
    const ingredients = []
    for (const recipeIngredient of recipe.getIngredients()) {
      const ingredient = await this.ingredientRepository.findById(
        recipeIngredient.getIngredientId()
      )
      if (!ingredient) {
        throw new IngredientNotFoundException(recipeIngredient.getIngredientId().getValue())
      }
      ingredients.push(ingredient)
    }

    // Calculate recipe cost
    const recipeCost = this.costCalculationService.calculateRecipeCost(recipe, ingredients)

    // Update recipe with calculated cost (optional - for caching purposes)
    recipe.updateTotalCost(recipeCost.getTotalCost())
    await this.recipeRepository.update(recipe)

    // Return cost DTO
    return RecipeMapper.recipeCostToDto(recipeCost)
  }
}
