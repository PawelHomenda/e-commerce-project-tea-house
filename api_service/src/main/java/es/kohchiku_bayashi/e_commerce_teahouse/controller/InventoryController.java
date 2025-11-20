package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Inventory;
import es.kohchiku_bayashi.e_commerce_teahouse.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.findById(id));
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.findByProductId(productId));
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStockProducts() {
        return ResponseEntity.ok(inventoryService.findLowStockProducts());
    }
    
    @PostMapping
    public ResponseEntity<Inventory> createInventory(@Valid @RequestBody Inventory inventory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.save(inventory));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.update(id, inventory));
    }
    
    @PatchMapping("/product/{productId}/add-stock")
    public ResponseEntity<Inventory> addStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.updateStock(productId, quantity));
    }
    
    @PatchMapping("/product/{productId}/reduce-stock")
    public ResponseEntity<Inventory> reduceStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.reduceStock(productId, quantity));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
