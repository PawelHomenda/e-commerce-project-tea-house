package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Provider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProviderController {
    
    private final ProviderService providerService;
    
    @GetMapping
    public ResponseEntity<List<Provider>> getAllProviders() {
        return ResponseEntity.ok(providerService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.findById(id));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Provider> getProviderByEmail(@PathVariable String email) {
        return ResponseEntity.ok(providerService.findByEmail(email));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Provider>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(providerService.findByName(name));
    }
    
    @PostMapping
    public ResponseEntity<Provider> createProvider(@Valid @RequestBody Provider provider) {
        return ResponseEntity.status(HttpStatus.CREATED).body(providerService.save(provider));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Provider> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody Provider provider) {
        return ResponseEntity.ok(providerService.update(id, provider));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        providerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}