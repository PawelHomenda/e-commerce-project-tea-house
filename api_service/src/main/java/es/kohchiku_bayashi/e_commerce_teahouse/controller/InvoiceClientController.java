package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.InvoiceClient;
import es.kohchiku_bayashi.e_commerce_teahouse.service.InvoiceClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<InvoiceClient>> getMyInvoices(@AuthenticationPrincipal Jwt jwt) {
        String OAuth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");

        if (scopes.contains("admin") || scopes.contains("user:employee")){
            return ResponseEntity.ok(invoiceClientService.findAll());
        }
        else if (scopes.contains("user:client")){
            return ResponseEntity.ok(invoiceClientService.findByClientOAuth2Id(OAuth2Id));
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<InvoiceClient>> getAllInvoices() {
        return ResponseEntity.ok(invoiceClientService.findAll());
    }
    
    // ✅ SEGURO: Validar propiedad de factura
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceClient> getInvoiceById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        InvoiceClient invoice = invoiceClientService.findById(id);
        
        // Admin y empleados ven todo
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(invoice);
        }
        
        // Cliente solo ve sus propias facturas
        if (scopes.contains("user:client")) {
            if (!invoice.getOrderClient().getClient().getOauth2Id().equals(oauth2Id)) {
                throw new AccessDeniedException("No tienes acceso a esta factura");
            }
            return ResponseEntity.ok(invoice);
        }
        
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }
    
    // ✅ SEGURO: Solo admin y empleados ven pendientes
    @GetMapping("/pending")
    public ResponseEntity<List<InvoiceClient>> getPendingPayments(
            @AuthenticationPrincipal Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        if (scopes.contains("admin") || scopes.contains("employee")) {
            return ResponseEntity.ok(invoiceClientService.findPendingPayments());
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }
    
    // ✅ SEGURO: Solo admin y empleados ven ingresos totales
    @GetMapping("/total-income")
    public ResponseEntity<Double> getTotalIncome(
            @AuthenticationPrincipal Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        if (scopes.contains("admin") || scopes.contains("employee")) {
            return ResponseEntity.ok(invoiceClientService.getTotalIncome());
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }
    
    // ✅ SEGURO: Solo admin y empleados ven ingresos mensuales
    @GetMapping("/total-income/month/{month}")
    public ResponseEntity<Double> getTotalIncomeByMonth(
            @PathVariable int month,
            @AuthenticationPrincipal Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        if (scopes.contains("admin") || scopes.contains("employee")) {
            return ResponseEntity.ok(invoiceClientService.getTotalIncomeByMonth(month));
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
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
