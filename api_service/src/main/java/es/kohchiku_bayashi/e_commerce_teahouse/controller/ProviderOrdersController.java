package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.OrderProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders/providers/my-orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProviderOrdersController {
    
    private final OrderProviderService orderProviderService;
    
    // ✅ SEGURO: Proveedor solo ve sus órdenes
    @GetMapping
    public ResponseEntity<List<OrderProvider>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(orderProviderService.findByProviderOauth2Id(oauth2Id));
    }
    
    // ✅ SEGURO: Proveedor solo ve su orden si es suya
    @GetMapping("/{id}")
    public ResponseEntity<OrderProvider> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        OrderProvider order = orderProviderService.findById(id);
        
        if (!order.getProvider().getOauth2Id().equals(oauth2Id) && 
            !jwt.getClaimAsStringList("scope").contains("admin")) {
            throw new AccessDeniedException("No tienes acceso a esta orden");
        }
        return ResponseEntity.ok(order);
    }
    
    // ✅ SEGURO: Filtrar por rango de fechas (solo sus órdenes)
    @GetMapping("/date-range")
    public ResponseEntity<List<OrderProvider>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<OrderProvider> orders = orderProviderService.findByProviderOauth2Id(oauth2Id);
        return ResponseEntity.ok(orders.stream()
                .filter(o -> !o.getOrderDate().isBefore(startDate) && 
                           !o.getOrderDate().isAfter(endDate))
                .toList());
    }
}
