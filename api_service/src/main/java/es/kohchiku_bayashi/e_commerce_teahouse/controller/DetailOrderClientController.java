package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.DetailOrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import es.kohchiku_bayashi.e_commerce_teahouse.service.DetailOrderClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/details/orders/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DetailOrderClientController {
    
    private final DetailOrderClientService detailOrderClientService;
    
    @GetMapping
    public ResponseEntity<List<DetailOrderClient>> getAllDetails() {
        return ResponseEntity.ok(detailOrderClientService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DetailOrderClient> getDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(detailOrderClientService.findById(id));
    }
    
    @GetMapping("/statistics/top5-revenue")
    public ResponseEntity<List<Object[]>> getTop5ProductsByRevenue() {
        return ResponseEntity.ok(detailOrderClientService.getTop5ProductsByRevenue());
    }
    
    @GetMapping("/statistics/top5-quantity")
    public ResponseEntity<List<Object[]>> getTop5ProductsByQuantity() {
        return ResponseEntity.ok(detailOrderClientService.getTop5ProductsByQuantity());
    }
    
    @GetMapping("/statistics/by-service-type")
    public ResponseEntity<List<Object[]>> getProductCountByServiceType() {
        return ResponseEntity.ok(detailOrderClientService.getProductCountByServiceType());
    }
    
    @GetMapping("/statistics/products-without-sales")
    public ResponseEntity<List<Product>> getProductsWithoutSales() {
        return ResponseEntity.ok(detailOrderClientService.findProductsWithoutSales());
    }
    
    @PostMapping
    public ResponseEntity<DetailOrderClient> createDetail(@Valid @RequestBody DetailOrderClient detail) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detailOrderClientService.save(detail));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DetailOrderClient> updateDetail(
            @PathVariable Long id,
            @Valid @RequestBody DetailOrderClient detail) {
        return ResponseEntity.ok(detailOrderClientService.update(id, detail));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetail(@PathVariable Long id) {
        detailOrderClientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
