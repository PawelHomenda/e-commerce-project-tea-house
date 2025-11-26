package es.kohchiku_bayashi.e_commerce_teahouse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "details_order_provider")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailOrderProvider {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ✅ @JsonBackReference: NO serializa el pedido (evita ciclo)
    @ManyToOne
    @JoinColumn(name = "id_order_provider", nullable = false)
    @JsonBackReference(value = "orderprovider-details")
    private OrderProvider orderProvider;
    
    // ✅ Mostramos el producto pero ignoramos sus colecciones
    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    @JsonIgnoreProperties({"inventory", "detailOrderProviders", "detailOrderClients"})
    private Product product;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1)
    @Column(nullable = false)
    private Integer quantity;
    
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "unit_price")
    private Double unitPrice;
    
    public Double getSubtotal() {
        if (quantity != null && unitPrice != null) {
            return quantity * unitPrice;
        }
        return 0.0;
    }
}