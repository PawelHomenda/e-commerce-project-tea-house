package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.InvoiceProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.PaymentState;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.InvoiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceProviderService {
    
    private final InvoiceProviderRepository invoiceProviderRepository;
    
    public List<InvoiceProvider> findAll() {
        return invoiceProviderRepository.findAll();
    }
    
    public InvoiceProvider findById(Long id) {
        return invoiceProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));
    }
    
    public InvoiceProvider save(InvoiceProvider invoiceProvider) {
        return invoiceProviderRepository.save(invoiceProvider);
    }
    
    public InvoiceProvider update(Long id, InvoiceProvider invoiceProvider) {
        InvoiceProvider existing = findById(id);
        
        existing.setInvoiceNumber(invoiceProvider.getInvoiceNumber());
        existing.setInvoiceDate(invoiceProvider.getInvoiceDate());
        existing.setTotal(invoiceProvider.getTotal());
        existing.setPaymentState(invoiceProvider.getPaymentState());
        existing.setPaymentDate(invoiceProvider.getPaymentDate());
        
        return invoiceProviderRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!invoiceProviderRepository.existsById(id)) {
            throw new RuntimeException("Factura no encontrada con id: " + id);
        }
        invoiceProviderRepository.deleteById(id);
    }
    
    public List<InvoiceProvider> findPendingInvoices() {
        return invoiceProviderRepository.findPendingInvoicesWithDetails();
    }
    
    public InvoiceProvider markAsPaid(Long id, LocalDate paymentDate) {
        InvoiceProvider invoice = findById(id);
        invoice.setPaymentState(PaymentState.PAID);
        invoice.setPaymentDate(paymentDate);
        return invoiceProviderRepository.save(invoice);
    }

    public List<InvoiceProvider> findByProviderOAuth2Id(String oAuth2Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByProviderOAuth2Id'");
    }

    public List<InvoiceProvider> findByEmployeeOAuth2Id(String oAuth2Id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByEmployeeOAuth2Id'");
    }
}

