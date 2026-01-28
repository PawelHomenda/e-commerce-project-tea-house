package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.InvoiceProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.PaymentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceProviderRepository extends JpaRepository<InvoiceProvider, Long> {
    
    Optional<InvoiceProvider> findByInvoiceNumber(String invoiceNumber);
    
    List<InvoiceProvider> findByPaymentState(PaymentState paymentState);
    
    List<InvoiceProvider> findByPaymentDateIsNull();
    
    List<InvoiceProvider> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT ip FROM InvoiceProvider ip JOIN FETCH ip.orderProvider op JOIN FETCH op.provider WHERE ip.paymentState = 'PENDENT'")
    List<InvoiceProvider> findPendingInvoicesWithDetails();

    @Query("SELECT ip FROM InvoiceProvider ip WHERE ip.orderProvider.provider.oauth2Id = :oAuth2Id")
    List<InvoiceProvider> findByProviderOAuth2Id(@Param("oAuth2Id") String oAuth2Id);

    @Query("SELECT ip FROM InvoiceProvider ip WHERE ip.orderProvider.employee.oauth2Id = :oAuth2Id")
    List<InvoiceProvider> findByEmployeeOAuth2Id(@Param("oAuth2Id") String oAuth2Id);

    @Query("SELECT ip FROM InvoiceProvider ip WHERE ip.paymentState = 'PENDENT'")
    List<InvoiceProvider> findPendingInvoices();
}