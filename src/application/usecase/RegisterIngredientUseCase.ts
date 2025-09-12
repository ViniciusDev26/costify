import type { IngredientRepository } from '../contracts/IngredientRepository.js'
import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import { IngredientFactory } from '../factory/IngredientFactory.js'
import { IngredientMapper } from '../mapper/IngredientMapper.js'
import { RegisterIngredientCommand } from '../dto/command/RegisterIngredientCommand.js'
import { IngredientDto } from '../dto/entity/IngredientDto.js'
import { IngredientAlreadyExistsException } from '../errors/IngredientAlreadyExistsException.js'

export class RegisterIngredientUseCase {
  private readonly ingredientFactory: IngredientFactory

  constructor(
    private readonly ingredientRepository: IngredientRepository,
    idGenerator: IdGenerator
  ) {
    this.ingredientFactory = new IngredientFactory(idGenerator)
  }

  async execute(command: RegisterIngredientCommand): Promise<IngredientDto> {
    // Check if ingredient with the same name already exists
    const existingIngredient = await this.ingredientRepository.findByName(command.name)
    if (existingIngredient) {
      throw new IngredientAlreadyExistsException(command.name)
    }

    // Create new ingredient
    const ingredient = this.ingredientFactory.createFromCommand(command)

    // Save to repository
    await this.ingredientRepository.save(ingredient)

    // Return DTO
    return IngredientMapper.toDto(ingredient)
  }
}