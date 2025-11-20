package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.InvoiceProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.InvoiceProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices/providers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceProviderController {
    
    private final InvoiceProviderService invoiceProviderService;
    
    @GetMapping
    public ResponseEntity<List<InvoiceProvider>> getAllInvoices() {
        return ResponseEntity.ok(invoiceProviderService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceProvider> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceProviderService.findById(id));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<InvoiceProvider>> getPendingInvoices() {
        return ResponseEntity.ok(invoiceProviderService.findPendingInvoices());
    }
    
    @PostMapping
    public ResponseEntity<InvoiceProvider> createInvoice(@Valid @RequestBody InvoiceProvider invoiceProvider) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceProviderService.save(invoiceProvider));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceProvider> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceProvider invoiceProvider) {
        return ResponseEntity.ok(invoiceProviderService.update(id, invoiceProvider));
    }
    
    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<InvoiceProvider> markAsPaid(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate) {
        return ResponseEntity.ok(invoiceProviderService.markAsPaid(id, paymentDate));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceProviderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
