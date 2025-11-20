package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.InvoiceClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceClientRepository extends JpaRepository<InvoiceClient, Long> {
    
    Optional<InvoiceClient> findByInvoiceNumber(String invoiceNumber);
    
    List<InvoiceClient> findByPaymentMethod(PaymentMethod paymentMethod);
    
    List<InvoiceClient> findByPaymentDateIsNull();
    
    List<InvoiceClient> findByPaymentDateIsNotNull();
    
    List<InvoiceClient> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(ic.total) FROM InvoiceClient ic")
    Double getTotalIncome();
    
    @Query("SELECT SUM(ic.total) FROM InvoiceClient ic WHERE MONTH(ic.invoiceDate) = :month")
    Double getTotalIncomeByMonth(@Param("month") int month);
}
