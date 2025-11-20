package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ProductCategory;
import es.kohchiku_bayashi.e_commerce_teahouse.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable ProductCategory category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        return ResponseEntity.ok(productService.findActiveProducts());
    }
    
    @GetMapping("/inactive")
    public ResponseEntity<List<Product>> getInactiveProducts() {
        return ResponseEntity.ok(productService.findInactiveProducts());
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return ResponseEntity.ok(productService.findByPriceRange(minPrice, maxPrice));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.findByName(name));
    }
    
    @GetMapping("/sorted/asc")
    public ResponseEntity<List<Product>> getProductsSortedByPriceAsc() {
        return ResponseEntity.ok(productService.findAllOrderByPriceAsc());
    }
    
    @GetMapping("/sorted/desc")
    public ResponseEntity<List<Product>> getProductsSortedByPriceDesc() {
        return ResponseEntity.ok(productService.findAllOrderByPriceDesc());
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(id, product));
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Product> deactivateProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deactivateProduct(id));
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Product> activateProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.activateProduct(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
