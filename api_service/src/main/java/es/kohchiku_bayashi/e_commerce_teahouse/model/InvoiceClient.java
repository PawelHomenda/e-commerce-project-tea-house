package es.kohchiku_bayashi.e_commerce_teahouse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "invoices_clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceClient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ✅ @JsonBackReference: NO serializa el pedido (evita ciclo)
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JoinColumn(name = "id_order_client", nullable = false)
    @JsonBackReference(value = "orderclient-invoice")
    private OrderClient orderClient;
    
    @Size(max = 20)
    @Column(name = "invoice_number", unique = true, length = 20)
    private String invoiceNumber;
    
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;
    
    @DecimalMin(value = "0.0", inclusive = false)
    @Column
    private Double total;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", columnDefinition = "VARCHAR(20) DEFAULT 'METALIC'")
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.METALIC;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    public boolean isPaid() {
        return paymentDate != null;
    }

    public OrderClient getOrderClient() {
        return orderClient;
    }
}
