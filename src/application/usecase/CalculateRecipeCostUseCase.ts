import type { RecipeRepository } from '../contracts/RecipeRepository.js'
import type { IngredientRepository } from '../contracts/IngredientRepository.js'
import { Id } from '@domain/valueobjects/Id.js'
import { RecipeMapper } from '../mapper/RecipeMapper.js'
import { RecipeCostCalculationService } from '@domain/services/RecipeCostCalculationService.js'
import { CalculateRecipeCostCommand } from '../dto/command/CalculateRecipeCostCommand.js'
import { RecipeCostDto } from '../dto/response/RecipeCostDto.js'
import { RecipeNotFoundException } from '../errors/RecipeNotFoundException.js'
import { IngredientNotFoundException } from '../errors/IngredientNotFoundException.js'

export class CalculateRecipeCostUseCase {
  private readonly costCalculationService: RecipeCostCalculationService

  constructor(
    private readonly recipeRepository: RecipeRepository,
    private readonly ingredientRepository: IngredientRepository
  ) {
    this.costCalculationService = new RecipeCostCalculationService()
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
