package es.kohchiku_bayashi.e_commerce_teahouse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.OrderState;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ServiceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders_clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderClient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ✅ Cliente que realiza el pedido (vinculado a OAuth2)
    @NotNull(message = "El cliente es obligatorio para crear una orden")
    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    @JsonIgnoreProperties({"orderClients"})
    private Client client;
    
    // ✅ Empleado que gestiona el pedido (puede ser nulo si está pendiente)
    @ManyToOne
    @JoinColumn(name = "id_employee")
    @JsonIgnoreProperties({"orderClients", "orderProviders"})
    private Employee employee;
    
    @NotNull(message = "La fecha del pedido es obligatoria")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    
    @NotNull(message = "El estado del pedido es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_state", nullable = false)
    private OrderState orderState;
    
    @NotNull(message = "El tipo de servicio es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;
    
    // ✅ Descuento global del pedido completo (0-100%)
    @Min(value = 0)
    @Max(value = 100)
    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;
    
    // ✅ @JsonManagedReference: SÍ serializa la factura
    @OneToOne(mappedBy = "orderClient")
    @JsonManagedReference(value = "orderclient-invoice")
    @ToString.Exclude
    private InvoiceClient invoiceClient;
    
    // ✅ @JsonManagedReference: SÍ serializa los detalles
    @OneToMany(mappedBy = "orderClient", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "orderclient-details")
    @ToString.Exclude
    private List<DetailOrderClient> detailOrderClients;
    
    // ✅ Calcula el subtotal sin descuento
    @Transient
    public Double getSubtotal() {
        if (detailOrderClients == null || detailOrderClients.isEmpty()) {
            return 0.0;
        }
        return detailOrderClients.stream()
                .mapToDouble(DetailOrderClient::getSubtotal)
                .sum();
    }
    
    // ✅ Calcula el total con descuento global
    @Transient
    public Double getTotal() {
        Double subtotal = getSubtotal();
        if (discountPercentage == null || discountPercentage == 0) {
            return subtotal;
        }
        return subtotal - (subtotal * discountPercentage / 100);
    }
}
