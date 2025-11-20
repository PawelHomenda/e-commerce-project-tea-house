package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.OrderProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    
    @GetMapping
    public ResponseEntity<List<OrderProvider>> getAllOrders() {
        return ResponseEntity.ok(orderProviderService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderProvider> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderProviderService.findById(id));
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
