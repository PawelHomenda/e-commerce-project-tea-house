package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.InvoiceClient;
import es.kohchiku_bayashi.e_commerce_teahouse.service.InvoiceClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceClientController {
    
    private final InvoiceClientService invoiceClientService;
    
    @GetMapping
    public ResponseEntity<List<InvoiceClient>> getAllInvoices() {
        return ResponseEntity.ok(invoiceClientService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceClient> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceClientService.findById(id));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<InvoiceClient>> getPendingPayments() {
        return ResponseEntity.ok(invoiceClientService.findPendingPayments());
    }
    
    @GetMapping("/total-income")
    public ResponseEntity<Double> getTotalIncome() {
        return ResponseEntity.ok(invoiceClientService.getTotalIncome());
    }
    
    @GetMapping("/total-income/month/{month}")
    public ResponseEntity<Double> getTotalIncomeByMonth(@PathVariable int month) {
        return ResponseEntity.ok(invoiceClientService.getTotalIncomeByMonth(month));
    }
    
    @PostMapping
    public ResponseEntity<InvoiceClient> createInvoice(@Valid @RequestBody InvoiceClient invoiceClient) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceClientService.save(invoiceClient));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceClient> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceClient invoiceClient) {
        return ResponseEntity.ok(invoiceClientService.update(id, invoiceClient));
    }
    
    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<InvoiceClient> markAsPaid(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate) {
        return ResponseEntity.ok(invoiceClientService.markAsPaid(id, paymentDate));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceClientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
