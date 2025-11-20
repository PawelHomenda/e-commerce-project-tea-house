package es.kohchiku_bayashi.e_commerce_teahouse.model;

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
    
    @OneToOne
    @JoinColumn(name = "id_order_client", nullable = false)
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
    @Column(name = "payment_method", columnDefinition = "VARCHAR(20) DEFAULT 'METALICO'")
    private PaymentMethod paymentMethod = PaymentMethod.METALICO;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    public boolean isPaid() {
        return paymentDate != null;
    }
}
