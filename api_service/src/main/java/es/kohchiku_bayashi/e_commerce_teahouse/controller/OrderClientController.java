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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderClientController {
    
    private final OrderClientService orderClientService;
    
    @GetMapping
    public ResponseEntity<List<OrderClient>> getAllOrders() {
        return ResponseEntity.ok(orderClientService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderClient> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderClientService.findById(id));
    }
    
    @GetMapping("/state/{state}")
    public ResponseEntity<List<OrderClient>> getOrdersByState(@PathVariable OrderState state) {
        return ResponseEntity.ok(orderClientService.findByOrderState(state));
    }
    
    @GetMapping("/service-type/{type}")
    public ResponseEntity<List<OrderClient>> getOrdersByServiceType(@PathVariable ServiceType type) {
        return ResponseEntity.ok(orderClientService.findByServiceType(type));
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<OrderClient>> getActiveOrders() {
        return ResponseEntity.ok(orderClientService.findActiveOrders());
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<OrderClient>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(orderClientService.findByDateRange(startDate, endDate));
    }
    
    @PostMapping
    public ResponseEntity<OrderClient> createOrder(@Valid @RequestBody OrderClient orderClient) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderClientService.save(orderClient));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OrderClient> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderClient orderClient) {
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
