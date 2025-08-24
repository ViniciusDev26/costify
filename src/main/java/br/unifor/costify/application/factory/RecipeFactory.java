package br.unifor.costify.application.factory;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;

import java.util.List;

public class RecipeFactory {
    
    private final IdGenerator idGenerator;
    
    public RecipeFactory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
    
    public Recipe create(String name, List<RecipeIngredient> ingredients) {
        return createRecipe(idGenerator, name, ingredients);
    }
    
    public Recipe create(Id id, String name, List<RecipeIngredient> ingredients) {
        return createRecipe(id, name, ingredients);
    }
    
    private Recipe createRecipe(IdGenerator idGenerator, String name, List<RecipeIngredient> ingredients) {
        return new Recipe(idGenerator, name, ingredients);
    }
    
    private Recipe createRecipe(Id id, String name, List<RecipeIngredient> ingredients) {
        return new Recipe(id, name, ingredients);
    }
}