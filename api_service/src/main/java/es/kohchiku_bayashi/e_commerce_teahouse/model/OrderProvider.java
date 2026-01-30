package es.kohchiku_bayashi.e_commerce_teahouse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders_providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProvider {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ✅ Mostramos el proveedor pero ignoramos sus colecciones
    @NotNull(message = "El proveedor es obligatorio para crear una orden")
    @ManyToOne
    @JoinColumn(name = "id_provider", nullable = false)
    @JsonIgnoreProperties({"orderProviders"})
    private Provider provider;
    
    // ✅ Mostramos el empleado pero ignoramos sus colecciones
    @NotNull(message = "El empleado es obligatorio para crear una orden")
    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    @JsonIgnoreProperties({"orderClients", "orderProviders"})
    private Employee employee;
    
    @NotNull(message = "La fecha del pedido es obligatoria")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    
    @DecimalMin(value = "0.0", inclusive = false)
    @Column
    private Double total;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    // ✅ Descuento global del pedido completo (0-100%)
    @Min(value = 0)
    @Max(value = 100)
    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;
    
    // ✅ @JsonManagedReference: SÍ serializa la factura
    @OneToOne(mappedBy = "orderProvider")
    @JsonManagedReference(value = "orderprovider-invoice")
    @ToString.Exclude
    private InvoiceProvider invoiceProvider;
    
    // ✅ @JsonManagedReference: SÍ serializa los detalles
    @OneToMany(mappedBy = "orderProvider", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "orderprovider-details")
    @ToString.Exclude
    private List<DetailOrderProvider> detailOrderProviders;
    
    // ✅ Calcula el subtotal sin descuento
    @Transient
    public Double getSubtotal() {
        if (detailOrderProviders == null || detailOrderProviders.isEmpty()) {
            return 0.0;
        }
        return detailOrderProviders.stream()
                .mapToDouble(DetailOrderProvider::getSubtotal)
                .sum();
    }
    
    // ✅ Calcula el total con descuento global
    @Transient
    public Double getTotalWithDiscount() {
        Double subtotal = getSubtotal();
        if (discountPercentage == null || discountPercentage == 0) {
            return subtotal;
        }
        return subtotal - (subtotal * discountPercentage / 100);
    }
    
    // ✅ Método para recalcular y actualizar el total persistido
    public void recalculateTotal() {
        this.total = getTotalWithDiscount();
    }
}