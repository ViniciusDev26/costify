import { Recipe } from '@domain/entities/Recipe.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import { RecipeIngredient } from '@domain/valueobjects/RecipeIngredient.js'
import { UnitUtils } from '@domain/valueobjects/Unit.js'
import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import { RegisterRecipeCommand } from '../dto/command/RegisterRecipeCommand.js'

export class RecipeFactory {
  constructor(private readonly idGenerator: IdGenerator) {}

  createFromCommand(command: RegisterRecipeCommand): Recipe {
    const id = new Id(this.idGenerator.generate())

    const ingredients = command.ingredients.map((ingredientCommand) => {
      const ingredientId = new Id(ingredientCommand.ingredientId)
      const unit =
        typeof ingredientCommand.unit === 'string'
          ? UnitUtils.fromString(ingredientCommand.unit)
          : ingredientCommand.unit

      return new RecipeIngredient(ingredientId, ingredientCommand.quantity, unit)
    })

    // Initial total cost will be calculated by use case
    const initialTotalCost = Money.zero()

    return new Recipe(id, command.name, ingredients, initialTotalCost)
  }
}
