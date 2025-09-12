import { Ingredient } from '@domain/entities/Ingredient.js'
import { IngredientDto } from '../dto/entity/IngredientDto.js'

export class IngredientMapper {
  static toDto(ingredient: Ingredient): IngredientDto {
    return {
      id: ingredient.getId().getValue(),
      name: ingredient.getName(),
      pricePerUnit: ingredient.getPricePerUnit().toFixed(2),
      unit: ingredient.getUnit(),
    }
  }

  static toDtoList(ingredients: Ingredient[]): IngredientDto[] {
    return ingredients.map(ingredient => this.toDto(ingredient))
  }
}