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
    
    // ✅ Mostramos el empleado pero ignoramos sus colecciones
    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
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
    
    // ✅ @JsonManagedReference: SÍ serializa la factura
    @OneToOne(mappedBy = "orderClient", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "orderclient-invoice")
    @ToString.Exclude
    private InvoiceClient invoiceClient;
    
    // ✅ @JsonManagedReference: SÍ serializa los detalles
    @OneToMany(mappedBy = "orderClient", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "orderclient-details")
    @ToString.Exclude
    private List<DetailOrderClient> detailOrderClients;
}
