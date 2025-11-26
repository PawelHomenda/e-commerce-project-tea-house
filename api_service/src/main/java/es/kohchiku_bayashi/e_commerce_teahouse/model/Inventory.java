package es.kohchiku_bayashi.e_commerce_teahouse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ✅ @JsonBackReference: NO serializa el producto (evita el ciclo)
    @OneToOne
    @JoinColumn(name = "id_product", nullable = false)
    @JsonBackReference(value = "product-inventory")
    private Product product;
    
    @NotNull(message = "La cantidad actual es obligatoria")
    @Min(value = 0)
    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity;
    
    @NotNull(message = "La cantidad mínima es obligatoria")
    @Min(value = 0)
    @Column(name = "minimum_quantity", nullable = false)
    private Integer minimumQuantity;
    
    public boolean isLowStock() {
        return currentQuantity < minimumQuantity;
    }
}
