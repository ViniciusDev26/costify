package br.unifor.costify.infra.data.entities;

import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredientTable {
  @jakarta.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Integer id;

  @Column(nullable = false)
  public String recipeId;

  @Column(nullable = false)
  public String ingredientId;

  @Column(nullable = false)
  public BigDecimal quantity;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(nullable = false, columnDefinition = "measurement_unit")
  public Unit unit;

  @CreatedDate
  public LocalDateTime createdAt;

  public static RecipeIngredientTable fromDomain(String recipeId, RecipeIngredient recipeIngredient) {
    RecipeIngredientTable table = new RecipeIngredientTable();
    table.recipeId = recipeId;
    table.ingredientId = recipeIngredient.getIngredientId().getValue();
    table.quantity = new BigDecimal(recipeIngredient.getQuantity());
    table.unit = recipeIngredient.getUnit();
    return table;
  }

  public static RecipeIngredient toDomain(RecipeIngredientTable raw) {
    return new RecipeIngredient(
        Id.of(raw.ingredientId),
        raw.quantity.doubleValue(),
        raw.unit
    );
  }
}