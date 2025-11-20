package es.kohchiku_bayashi.e_commerce_teahouse.model;

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
    
    @ManyToOne
    @JoinColumn(name = "id_provider", nullable = false)
    private Provider provider;
    
    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private Employee employee;
    
    @NotNull(message = "La fecha del pedido es obligatoria")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(precision = 8, scale = 2)
    private BigDecimal total;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    @OneToOne(mappedBy = "orderProvider", cascade = CascadeType.ALL)
    @ToString.Exclude
    private InvoiceProvider invoiceProvider;
    
    @OneToMany(mappedBy = "orderProvider", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<DetailOrderProvider> detailOrderProviders;
}
