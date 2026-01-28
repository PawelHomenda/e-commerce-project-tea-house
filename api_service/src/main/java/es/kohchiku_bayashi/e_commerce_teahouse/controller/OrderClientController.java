package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.OrderState;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ServiceType;
import es.kohchiku_bayashi.e_commerce_teahouse.service.OrderClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderClientController {
    
    private final OrderClientService orderClientService;
    
    // ✅ SEGURO: Solo ADMIN ve todos los pedidos
    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderClient>> getAllOrders() {
        return ResponseEntity.ok(orderClientService.findAll());
    }
    
    // ✅ SEGURO: Cliente ve solo sus pedidos, ADMIN/EMPLOYEE ve todos
    @GetMapping
    public ResponseEntity<List<OrderClient>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        // Si es admin o employee, devuelve todos los pedidos
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(orderClientService.findAll());
        }
        
        // Si no es admin/employee, devuelve solo sus pedidos
        return ResponseEntity.ok(orderClientService.findByClientOauth2Id(oauth2Id));
    }
    
    // ✅ SEGURO: Cliente solo puede ver su propio pedido, ADMIN/EMPLOYEE ve todo
    @GetMapping("/{id}")
    public ResponseEntity<OrderClient> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        OrderClient order = orderClientService.findById(id);
        
        // ADMIN/EMPLOYEE ven todo
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(order);
        }
        
        // Cliente solo ve su propio pedido
        if (!order.getClient().getOauth2Id().equals(oauth2Id)) {
            throw new AccessDeniedException("No tienes acceso a este pedido");
        }
        return ResponseEntity.ok(order);
    }
    
    // ✅ SEGURO: Filtrar por estado (solo pedidos del usuario)
    @GetMapping("/state/{state}")
    public ResponseEntity<List<OrderClient>> getOrdersByState(
            @PathVariable OrderState state,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<OrderClient> orders = orderClientService.findByClientOauth2Id(oauth2Id);
        return ResponseEntity.ok(orders.stream()
                .filter(o -> o.getOrderState() == state)
                .toList());
    }
    
    // ✅ SEGURO: Filtrar por tipo de servicio (solo pedidos del usuario)
    @GetMapping("/service-type/{type}")
    public ResponseEntity<List<OrderClient>> getOrdersByServiceType(
            @PathVariable ServiceType type,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<OrderClient> orders = orderClientService.findByClientOauth2Id(oauth2Id);
        return ResponseEntity.ok(orders.stream()
                .filter(o -> o.getServiceType() == type)
                .toList());
    }
    
    // ✅ SEGURO: Pedidos activos (solo del usuario)
    @GetMapping("/active")
    public ResponseEntity<List<OrderClient>> getActiveOrders(
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<OrderClient> orders = orderClientService.findByClientOauth2Id(oauth2Id);
        return ResponseEntity.ok(orders.stream()
                .filter(o -> o.getOrderState() != OrderState.DELIVERED && 
                           o.getOrderState() != OrderState.CANCELED)
                .toList());
    }
    
    // ✅ SEGURO: Rango de fechas (solo del usuario)
    @GetMapping("/date-range")
    public ResponseEntity<List<OrderClient>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<OrderClient> orders = orderClientService.findByClientOauth2Id(oauth2Id);
        return ResponseEntity.ok(orders.stream()
                .filter(o -> !o.getOrderDate().isBefore(startDate) && 
                           !o.getOrderDate().isAfter(endDate))
                .toList());
    }
    
    @PostMapping
    public ResponseEntity<OrderClient> createOrder(
            @Valid @RequestBody OrderClient orderClient,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderClientService.save(orderClient));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OrderClient> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderClient orderClient,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(orderClientService.update(id, orderClient));
    }
    
    @PatchMapping("/{id}/state")
    public ResponseEntity<OrderClient> updateOrderState(
            @PathVariable Long id,
            @RequestParam OrderState newState) {
        return ResponseEntity.ok(orderClientService.updateOrderState(id, newState));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderClientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
