package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.InvoiceClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.PaymentMethod;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.InvoiceClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceClientService {
    
    private final InvoiceClientRepository invoiceClientRepository;
    
    public List<InvoiceClient> findAll() {
        return invoiceClientRepository.findAll();
    }
    
    public InvoiceClient findById(Long id) {
        return invoiceClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));
    }
    
    public InvoiceClient save(InvoiceClient invoiceClient) {
        return invoiceClientRepository.save(invoiceClient);
    }
    
    public InvoiceClient update(Long id, InvoiceClient invoiceClient) {
        InvoiceClient existing = findById(id);
        
        existing.setInvoiceNumber(invoiceClient.getInvoiceNumber());
        existing.setInvoiceDate(invoiceClient.getInvoiceDate());
        existing.setTotal(invoiceClient.getTotal());
        existing.setPaymentMethod(invoiceClient.getPaymentMethod());
        existing.setPaymentDate(invoiceClient.getPaymentDate());
        
        return invoiceClientRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!invoiceClientRepository.existsById(id)) {
            throw new RuntimeException("Factura no encontrada con id: " + id);
        }
        invoiceClientRepository.deleteById(id);
    }
    
    public List<InvoiceClient> findPendingPayments() {
        return invoiceClientRepository.findByPaymentDateIsNull();
    }
    
    public Double getTotalIncome() {
        return invoiceClientRepository.getTotalIncome();
    }
    
    public Double getTotalIncomeByMonth(int month) {
        return invoiceClientRepository.getTotalIncomeByMonth(month);
    }
    
    public InvoiceClient markAsPaid(Long id, LocalDate paymentDate) {
        InvoiceClient invoice = findById(id);
        invoice.setPaymentDate(paymentDate);
        return invoiceClientRepository.save(invoice);
    }

    public  List<InvoiceClient> findByClientOAuth2Id(String oAuth2Id) {
        return invoiceClientRepository.findByClientOAuth2Id(oAuth2Id);
    }

    public  List<InvoiceClient> findByEmployeeOAuth2Id(String oAuth2Id) {
        return invoiceClientRepository.findByEmployeeOAuth2Id(oAuth2Id);
    }
}
