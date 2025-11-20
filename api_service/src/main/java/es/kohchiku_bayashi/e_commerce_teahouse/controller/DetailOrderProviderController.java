package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.DetailOrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.DetailOrderProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/details/orders/providers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DetailOrderProviderController {
    
    private final DetailOrderProviderService detailOrderProviderService;
    
    @GetMapping
    public ResponseEntity<List<DetailOrderProvider>> getAllDetails() {
        return ResponseEntity.ok(detailOrderProviderService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DetailOrderProvider> getDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(detailOrderProviderService.findById(id));
    }
    
    @GetMapping("/statistics/products-purchased")
    public ResponseEntity<List<Object[]>> getTotalProductsPurchased() {
        return ResponseEntity.ok(detailOrderProviderService.getTotalProductsPurchased());
    }
    
    @GetMapping("/statistics/products-purchased/month/{month}")
    public ResponseEntity<List<Object[]>> getTotalProductsPurchasedByMonth(@PathVariable int month) {
        return ResponseEntity.ok(detailOrderProviderService.getTotalProductsPurchasedByMonth(month));
    }
    
    @PostMapping
    public ResponseEntity<DetailOrderProvider> createDetail(@Valid @RequestBody DetailOrderProvider detail) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detailOrderProviderService.save(detail));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DetailOrderProvider> updateDetail(
            @PathVariable Long id,
            @Valid @RequestBody DetailOrderProvider detail) {
        return ResponseEntity.ok(detailOrderProviderService.update(id, detail));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetail(@PathVariable Long id) {
        detailOrderProviderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
