package com.restaurant.system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient implements Serializable {

    private static final long serialVersionUID = 5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(name = "stock_quantity", nullable = false, precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal stockQuantity = BigDecimal.ZERO;

    @Column(name = "cost_per_unit", precision = 12, scale = 4)
    @Builder.Default
    private BigDecimal costPerUnit = BigDecimal.ZERO;

    @Column(name = "min_stock_level", precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal minStockLevel = BigDecimal.ZERO;


    @OneToMany(mappedBy = "ingredient", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<DishIngredient> dishIngredients = new ArrayList<>();

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient)) return false;
        Ingredient that = (Ingredient) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Ingredient{id=" + id + ", name='" + name + "', unit='" + unit + "'}";
    }
}
