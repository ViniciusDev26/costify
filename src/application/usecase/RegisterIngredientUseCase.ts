import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import type { IngredientRepository } from '../contracts/IngredientRepository.js'
import type { RegisterIngredientCommand } from '../dto/command/RegisterIngredientCommand.js'
import type { IngredientDto } from '../dto/entity/IngredientDto.js'
import { IngredientAlreadyExistsException } from '../errors/IngredientAlreadyExistsException.js'
import { IngredientFactory } from '../factory/IngredientFactory.js'
import { IngredientMapper } from '../mapper/IngredientMapper.js'

export class RegisterIngredientUseCase {
  private readonly ingredientFactory: IngredientFactory

  constructor(
    private readonly ingredientRepository: IngredientRepository,
    private readonly idGenerator: IdGenerator
  ) {
    this.ingredientFactory = new IngredientFactory(this.idGenerator)
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
