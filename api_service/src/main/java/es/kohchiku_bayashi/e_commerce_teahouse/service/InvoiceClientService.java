package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.exception.ResourceNotFoundException;
import es.kohchiku_bayashi.e_commerce_teahouse.model.InvoiceClient;
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
    private final OrderClientService orderClientService;
    
    public List<InvoiceClient> findAll() {
        return invoiceClientRepository.findAll();
    }
    
    public InvoiceClient findById(Long id) {
        return invoiceClientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id: " + id));
    }
    
    public InvoiceClient save(InvoiceClient invoiceClient) {
        // ✅ Cargar la orden del cliente existente (obligatorio)
        if (invoiceClient.getOrderClient() != null && invoiceClient.getOrderClient().getId() != null) {
            invoiceClient.setOrderClient(orderClientService.findById(invoiceClient.getOrderClient().getId()));
        }
        
        // ✅ Establecer fecha de factura si no se proporciona
        if (invoiceClient.getInvoiceDate() == null) {
            invoiceClient.setInvoiceDate(LocalDate.now());
        }
        
        // ✅ Calcular total automáticamente del OrderClient si no se proporciona
        if (invoiceClient.getTotal() == null && invoiceClient.getOrderClient() != null) {
            invoiceClient.setTotal(invoiceClient.getOrderClient().getTotal());
        }
        
        return invoiceClientRepository.save(invoiceClient);
    }
    
    public InvoiceClient update(Long id, InvoiceClient invoiceClient) {
        InvoiceClient existing = findById(id);
        
        // ✅ Actualizar orden si se proporciona
        if (invoiceClient.getOrderClient() != null && invoiceClient.getOrderClient().getId() != null) {
            existing.setOrderClient(orderClientService.findById(invoiceClient.getOrderClient().getId()));
        }
        
        existing.setInvoiceNumber(invoiceClient.getInvoiceNumber());
        
        // ✅ Actualizar fecha de factura si se proporciona
        if (invoiceClient.getInvoiceDate() != null) {
            existing.setInvoiceDate(invoiceClient.getInvoiceDate());
        }
        
        // ✅ Actualizar total (si se proporciona, usar ese; sino usar el del OrderClient)
        if (invoiceClient.getTotal() != null) {
            existing.setTotal(invoiceClient.getTotal());
        } else if (existing.getOrderClient() != null) {
            existing.setTotal(existing.getOrderClient().getTotal());
        }
        
        existing.setPaymentMethod(invoiceClient.getPaymentMethod());
        existing.setPaymentDate(invoiceClient.getPaymentDate());
        
        return invoiceClientRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!invoiceClientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Factura no encontrada con id: " + id);
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
