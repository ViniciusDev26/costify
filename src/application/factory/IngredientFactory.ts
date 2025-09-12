import { Ingredient } from '@domain/entities/Ingredient.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import { Unit, UnitUtils } from '@domain/valueobjects/Unit.js'
import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import { RegisterIngredientCommand } from '../dto/command/RegisterIngredientCommand.js'
import { UpdateIngredientCommand } from '../dto/command/UpdateIngredientCommand.js'

export class IngredientFactory {
  constructor(private readonly idGenerator: IdGenerator) {}

  createFromCommand(command: RegisterIngredientCommand): Ingredient {
    const id = new Id(this.idGenerator.generate())
    const money = new Money(command.pricePerUnit)
    const unit =
      typeof command.unit === 'string' ? UnitUtils.fromString(command.unit) : command.unit

    return new Ingredient(id, command.name, money, unit)
  }

  updateFromCommand(existing: Ingredient, command: UpdateIngredientCommand): Ingredient {
    const updatedIngredient = new Ingredient(
      existing.getId(),
      command.name ?? existing.getName(),
      command.pricePerUnit ? new Money(command.pricePerUnit) : existing.getPricePerUnit(),
      command.unit
        ? typeof command.unit === 'string'
          ? UnitUtils.fromString(command.unit)
          : command.unit
        : existing.getUnit()
    )

    return updatedIngredient
  }
}
