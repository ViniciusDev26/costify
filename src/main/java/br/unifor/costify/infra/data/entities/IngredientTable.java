package br.unifor.costify.infra.data.entities;

import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "ingredients")
public class IngredientTable {
  @Id public String id;

  @Column(nullable = false)
  public String name;

  @Column(nullable = false)
  public BigDecimal packageQuantity;

  @Column(nullable = false)
  public BigDecimal packagePrice;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(nullable = false, columnDefinition = "measurement_unit")
  public Unit packageUnit;


  @CreatedDate public LocalDateTime createdAt;

  @UpdateTimestamp public LocalDateTime updatedAt;

  public static IngredientTable fromDomain(Ingredient ingredient) {
    IngredientTable table = new IngredientTable();
    table.id = ingredient.getId().getValue();
    table.name = ingredient.getName();
    table.packageQuantity = new BigDecimal(ingredient.getPackageQuantity());
    table.packagePrice = new BigDecimal(ingredient.getPackagePrice());
    table.packageUnit = ingredient.getPackageUnit();
    return table;
  }

  public static Ingredient toDomain(IngredientTable raw) {
    return new Ingredient(
        br.unifor.costify.domain.valueobject.Id.of(raw.id),
        raw.name,
        raw.packageQuantity.doubleValue(),
        raw.packagePrice.doubleValue(),
        raw.packageUnit);
  }
}
