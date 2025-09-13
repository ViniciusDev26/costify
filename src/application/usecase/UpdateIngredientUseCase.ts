import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import { Id } from '@domain/valueobjects/Id.js'
import type { IngredientRepository } from '../contracts/IngredientRepository.js'
import type { UpdateIngredientCommand } from '../dto/command/UpdateIngredientCommand.js'
import type { IngredientDto } from '../dto/entity/IngredientDto.js'
import { IngredientAlreadyExistsException } from '../errors/IngredientAlreadyExistsException.js'
import { IngredientNotFoundException } from '../errors/IngredientNotFoundException.js'
import { IngredientFactory } from '../factory/IngredientFactory.js'
import { IngredientMapper } from '../mapper/IngredientMapper.js'

export class UpdateIngredientUseCase {
  private readonly ingredientFactory: IngredientFactory

  constructor(
    private readonly ingredientRepository: IngredientRepository,
    private readonly idGenerator: IdGenerator
  ) {
    this.ingredientFactory = new IngredientFactory(this.idGenerator)
  }

  async execute(command: UpdateIngredientCommand): Promise<IngredientDto> {
    const id = new Id(command.id)

    // Find existing ingredient
    const existingIngredient = await this.ingredientRepository.findById(id)
    if (!existingIngredient) {
      throw new IngredientNotFoundException(command.id)
    }

    // Check if name is being updated and if it conflicts with another ingredient
    if (command.name && command.name !== existingIngredient.getName()) {
      const conflictingIngredient = await this.ingredientRepository.findByName(command.name)
      if (conflictingIngredient && !conflictingIngredient.getId().equals(id)) {
        throw new IngredientAlreadyExistsException(command.name)
      }
    }

    // Create updated ingredient
    const updatedIngredient = this.ingredientFactory.updateFromCommand(existingIngredient, command)

    // Save to repository
    await this.ingredientRepository.update(updatedIngredient)

    // Return DTO
    return IngredientMapper.toDto(updatedIngredient)
  }
}
