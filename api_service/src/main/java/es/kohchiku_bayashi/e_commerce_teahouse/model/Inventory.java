package es.kohchiku_bayashi.e_commerce_teahouse.model;

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
    
    @OneToOne
    @JoinColumn(name = "id_product", nullable = false)
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
