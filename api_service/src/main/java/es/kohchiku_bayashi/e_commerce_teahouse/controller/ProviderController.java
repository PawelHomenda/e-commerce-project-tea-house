package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Provider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProviderController {
    
    private final ProviderService providerService;
    
    // ✅ SEGURO: Solo ADMIN ve todos
    @GetMapping("/admin/all")
    public ResponseEntity<List<Provider>> getAllProviders() {
        return ResponseEntity.ok(providerService.findAll());
    }
    
    // ✅ SEGURO: Proveedor ve sus propios datos, ADMIN ve todos
    @GetMapping
    public ResponseEntity<?> getMyProvider(
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        // Si es admin o employee, devuelve todos los proveedores
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(providerService.findAll());
        }
        
        // Si no es admin/employee, devuelve solo su perfil
        return ResponseEntity.ok(providerService.findByOauth2Id(oauth2Id));
    }
    
    // ✅ SEGURO: Proveedor solo ve sus datos o ADMIN ve cualquiera
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Provider provider = providerService.findById(id);
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        // Admin y employees ven todo
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(provider);
        }
        
        // Proveedor solo ve sus datos
        if (!provider.getOauth2Id().equals(oauth2Id)) {
            throw new AccessDeniedException("No tienes acceso a este proveedor");
        }
        return ResponseEntity.ok(provider);
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
    public ResponseEntity<Provider> createProvider(
            @Valid @RequestBody Provider provider) {
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