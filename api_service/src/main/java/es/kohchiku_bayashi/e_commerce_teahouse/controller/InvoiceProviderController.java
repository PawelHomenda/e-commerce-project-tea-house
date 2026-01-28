package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.InvoiceProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.InvoiceProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<List<InvoiceProvider>> getMyInvoices(@AuthenticationPrincipal Jwt jwt) {
        String OAuth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        if (scopes.contains("admin") || scopes.contains("user:employee")){
            return ResponseEntity.ok(invoiceProviderService.findAll());
        }
        else if (scopes.contains("user:provider")){
            return ResponseEntity.ok(invoiceProviderService.findByProviderOAuth2Id(OAuth2Id));
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<InvoiceProvider>> getAllInvoices() {
        return ResponseEntity.ok(invoiceProviderService.findAll());
    }
    
    // ✅ SEGURO: Validar propiedad de factura
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceProvider> getInvoiceById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        InvoiceProvider invoice = invoiceProviderService.findById(id);
        
        // Admin y empleados ven todo
        if (scopes.contains("admin") || scopes.contains("employee")) {
            return ResponseEntity.ok(invoice);
        }
        
        // Proveedor solo ve sus propias facturas
        if (scopes.contains("user:provider")) {
            if (!invoice.getOrderProvider().getProvider().getOauth2Id().equals(oauth2Id)) {
                throw new AccessDeniedException("No tienes acceso a esta factura");
            }
            return ResponseEntity.ok(invoice);
        }
        
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }
    
    // ✅ SEGURO: Solo admin y empleados ven pendientes
    @GetMapping("/pending")
    public ResponseEntity<List<InvoiceProvider>> getPendingInvoices(
            @AuthenticationPrincipal Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(invoiceProviderService.findPendingInvoices());
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
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
