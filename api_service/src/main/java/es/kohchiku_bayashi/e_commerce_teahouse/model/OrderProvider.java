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
    @ManyToOne
    @JoinColumn(name = "id_provider", nullable = false)
    @JsonIgnoreProperties({"orderProviders"})
    private Provider provider;
    
    // ✅ Mostramos el empleado pero ignoramos sus colecciones
    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    @JsonIgnoreProperties({"orderClients", "orderProviders"})
    private Employee employee;
    
    @NotNull(message = "La fecha del pedido es obligatoria")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(precision = 8, scale = 2)
    private BigDecimal total;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    // ✅ @JsonManagedReference: SÍ serializa la factura
    @OneToOne(mappedBy = "orderProvider", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "orderprovider-invoice")
    @ToString.Exclude
    private InvoiceProvider invoiceProvider;
    
    // ✅ @JsonManagedReference: SÍ serializa los detalles
    @OneToMany(mappedBy = "orderProvider", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "orderprovider-details")
    @ToString.Exclude
    private List<DetailOrderProvider> detailOrderProviders;
}