package es.kohchiku_bayashi.e_commerce_teahouse.model;

import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.PaymentState;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "invoices_providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceProvider {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "id_order_provider", nullable = false)
    private OrderProvider orderProvider;
    
    @Size(max = 20)
    @Column(name = "invoice_number", unique = true, length = 20)
    private String invoiceNumber;
    
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;
    
    @DecimalMin(value = "0.0", inclusive = false)
    @Column
    private Double total;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_state")
    private PaymentState paymentState;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    public boolean isPaid() {
        return paymentState == PaymentState.PAGADO;
    }
}
