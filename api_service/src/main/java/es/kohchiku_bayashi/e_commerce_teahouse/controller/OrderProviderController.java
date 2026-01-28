package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.OrderProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders/providers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderProviderController {
    
    private final OrderProviderService orderProviderService;
    
    // ✅ SEGURO: Solo ADMIN y EMPLOYEE ven todos los pedidos de proveedores
    @GetMapping
    public ResponseEntity<List<OrderProvider>> getOrders(@AuthenticationPrincipal Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        String oauth2Id = jwt.getClaimAsString("sub");
        
        // Admin y empleados ven todos
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(orderProviderService.findAll());
        }
        
        // Proveedores ven solo sus pedidos
        if (scopes.contains("user:provider")) {
            return ResponseEntity.ok(orderProviderService.findByProviderOAuth2Id(oauth2Id));
        }
        
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }
    
    // ✅ SEGURO: Solo acceso a pedidos propios o si eres admin/employee
    @GetMapping("/{id}")
    public ResponseEntity<OrderProvider> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        String oauth2Id = jwt.getClaimAsString("sub");
        OrderProvider order = orderProviderService.findById(id);
        
        // Admin y empleados ven todo
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(order);
        }
        
        // Proveedor solo ve sus propios pedidos
        if (!order.getProvider().getOauth2Id().equals(oauth2Id)) {
            throw new AccessDeniedException("No tienes acceso a este pedido");
        }
        
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<OrderProvider>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(orderProviderService.findByDateRange(startDate, endDate));
    }
    
    @GetMapping("/month/{month}/year/{year}")
    public ResponseEntity<List<OrderProvider>> getOrdersByMonthAndYear(
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(orderProviderService.findByMonthAndYear(month, year));
    }
    
    @GetMapping("/total-cost")
    public ResponseEntity<BigDecimal> getTotalCost() {
        return ResponseEntity.ok(orderProviderService.getTotalCost());
    }
    
    @GetMapping("/total-cost/month/{month}")
    public ResponseEntity<BigDecimal> getTotalCostByMonth(@PathVariable int month) {
        return ResponseEntity.ok(orderProviderService.getTotalCostByMonth(month));
    }
    
    @PostMapping
    public ResponseEntity<OrderProvider> createOrder(@Valid @RequestBody OrderProvider orderProvider) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderProviderService.save(orderProvider));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OrderProvider> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderProvider orderProvider) {
        return ResponseEntity.ok(orderProviderService.update(id, orderProvider));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderProviderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
