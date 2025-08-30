package br.unifor.costify.infra.data.entities;

import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class RecipeTable {
  @jakarta.persistence.Id
  public String id;

  @Column(nullable = false)
  public String name;

  @Column(nullable = false)
  public BigDecimal totalCost;

  @OneToMany(mappedBy = "recipeId", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  public List<RecipeIngredientTable> ingredients = new ArrayList<>();

  @CreatedDate
  public LocalDateTime createdAt;

  @UpdateTimestamp
  public LocalDateTime updatedAt;

  public static RecipeTable fromDomain(Recipe recipe) {
    RecipeTable table = new RecipeTable();
    table.id = recipe.getId().getValue();
    table.name = recipe.getName();
    table.totalCost = recipe.getTotalCost().getAmount();
    
    table.ingredients = recipe.getIngredients().stream()
        .map(ingredient -> RecipeIngredientTable.fromDomain(recipe.getId().getValue(), ingredient))
        .toList();
    
    return table;
  }

  public static Recipe toDomain(RecipeTable raw) {
    List<RecipeIngredient> ingredients = raw.ingredients.stream()
        .map(RecipeIngredientTable::toDomain)
        .toList();

    return new Recipe(
        Id.of(raw.id),
        raw.name,
        ingredients,
        Money.of(raw.totalCost)
    );
  }
}