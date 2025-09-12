import type { RecipeRepository } from '../contracts/RecipeRepository.js'
import type { IngredientRepository } from '../contracts/IngredientRepository.js'
import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import { Id } from '@domain/valueobjects/Id.js'
import { RecipeFactory } from '../factory/RecipeFactory.js'
import { RecipeMapper } from '../mapper/RecipeMapper.js'
import { RecipeCostCalculationService } from '@domain/services/RecipeCostCalculationService.js'
import { RegisterRecipeCommand } from '../dto/command/RegisterRecipeCommand.js'
import { RecipeDto } from '../dto/entity/RecipeDto.js'
import { RecipeAlreadyExistsException } from '../errors/RecipeAlreadyExistsException.js'
import { IngredientNotFoundException } from '../errors/IngredientNotFoundException.js'

export class RegisterRecipeUseCase {
  private readonly recipeFactory: RecipeFactory
  private readonly costCalculationService: RecipeCostCalculationService

  constructor(
    private readonly recipeRepository: RecipeRepository,
    private readonly ingredientRepository: IngredientRepository,
    idGenerator: IdGenerator
  ) {
    this.recipeFactory = new RecipeFactory(idGenerator)
    this.costCalculationService = new RecipeCostCalculationService()
  }

  async execute(command: RegisterRecipeCommand): Promise<RecipeDto> {
    // Check if recipe with the same name already exists
    const existingRecipe = await this.recipeRepository.findByName(command.name)
    if (existingRecipe) {
      throw new RecipeAlreadyExistsException(command.name)
    }

    // Validate that all ingredients exist
    const ingredients = []
    for (const ingredientCommand of command.ingredients) {
      const ingredientId = new Id(ingredientCommand.ingredientId)
      const ingredient = await this.ingredientRepository.findById(ingredientId)
      if (!ingredient) {
        throw new IngredientNotFoundException(ingredientCommand.ingredientId)
      }
      ingredients.push(ingredient)
    }

    // Create new recipe
    const recipe = this.recipeFactory.createFromCommand(command)

    // Calculate total cost
    const recipeCost = this.costCalculationService.calculateRecipeCost(recipe, ingredients)
    recipe.updateTotalCost(recipeCost.getTotalCost())

    // Save to repository
    await this.recipeRepository.save(recipe)

    // Return DTO
    return RecipeMapper.toDto(recipe)
  }
}