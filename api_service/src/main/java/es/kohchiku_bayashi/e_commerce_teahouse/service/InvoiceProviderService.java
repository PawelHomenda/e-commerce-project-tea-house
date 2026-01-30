package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.exception.ResourceNotFoundException;
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
    private final OrderProviderService orderProviderService;
    
    public List<InvoiceProvider> findAll() {
        return invoiceProviderRepository.findAll();
    }
    
    public InvoiceProvider findById(Long id) {
        return invoiceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id: " + id));
    }
    
    public InvoiceProvider save(InvoiceProvider invoiceProvider) {
        // ✅ Cargar la orden del proveedor existente (obligatorio)
        if (invoiceProvider.getOrderProvider() != null && invoiceProvider.getOrderProvider().getId() != null) {
            invoiceProvider.setOrderProvider(orderProviderService.findById(invoiceProvider.getOrderProvider().getId()));
        }
        
        // ✅ Establecer fecha de factura si no se proporciona
        if (invoiceProvider.getInvoiceDate() == null) {
            invoiceProvider.setInvoiceDate(LocalDate.now());
        }
        
        // ✅ Calcular total automáticamente del OrderProvider si no se proporciona
        if (invoiceProvider.getTotal() == null && invoiceProvider.getOrderProvider() != null) {
            invoiceProvider.setTotal(invoiceProvider.getOrderProvider().getTotalWithDiscount());
        }
        
        return invoiceProviderRepository.save(invoiceProvider);
    }
    
    public InvoiceProvider update(Long id, InvoiceProvider invoiceProvider) {
        InvoiceProvider existing = findById(id);
        
        // ✅ Actualizar orden si se proporciona
        if (invoiceProvider.getOrderProvider() != null && invoiceProvider.getOrderProvider().getId() != null) {
            existing.setOrderProvider(orderProviderService.findById(invoiceProvider.getOrderProvider().getId()));
        }
        
        existing.setInvoiceNumber(invoiceProvider.getInvoiceNumber());
        
        // ✅ Actualizar fecha de factura si se proporciona
        if (invoiceProvider.getInvoiceDate() != null) {
            existing.setInvoiceDate(invoiceProvider.getInvoiceDate());
        }
        
        // ✅ Actualizar total (si se proporciona, usar ese; sino usar el del OrderProvider)
        if (invoiceProvider.getTotal() != null) {
            existing.setTotal(invoiceProvider.getTotal());
        } else if (existing.getOrderProvider() != null) {
            existing.setTotal(existing.getOrderProvider().getTotalWithDiscount());
        }
        
        existing.setPaymentState(invoiceProvider.getPaymentState());
        existing.setPaymentDate(invoiceProvider.getPaymentDate());
        
        return invoiceProviderRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!invoiceProviderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Factura no encontrada con id: " + id);
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

