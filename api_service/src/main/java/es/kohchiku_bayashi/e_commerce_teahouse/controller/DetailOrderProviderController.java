package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.DetailOrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.DetailOrderProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/details/orders/providers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DetailOrderProviderController {
    
    private final DetailOrderProviderService detailOrderProviderService;
    
    @GetMapping
    public ResponseEntity<List<DetailOrderProvider>> getMyDetails(@AuthenticationPrincipal Jwt jwt) {
        String OAuth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        if (scopes.contains("admin") || scopes.contains("user:employee")){
            return ResponseEntity.ok(detailOrderProviderService.findAll());
        }else if(scopes.contains("user:provider")){
            return ResponseEntity.ok(detailOrderProviderService.findByProviderOAuth2Id(OAuth2Id));
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<DetailOrderProvider>> getAllDetails() {
        return ResponseEntity.ok(detailOrderProviderService.findAll());
    }
    
    // ✅ SEGURO: Validar propiedad del detalle de orden
    @GetMapping("/{id}")
    public ResponseEntity<DetailOrderProvider> getDetailById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        DetailOrderProvider detail = detailOrderProviderService.findById(id);
        
        // Admin y empleados ven todo
        if (scopes.contains("admin") || scopes.contains("employee")) {
            return ResponseEntity.ok(detail);
        }
        
        // Proveedor solo ve detalles de sus propias órdenes
        if (scopes.contains("user:provider")) {
            if (detail.getOrderProvider().getProvider().getOauth2Id().equals(oauth2Id)) {
                return ResponseEntity.ok(detail);
            }
            throw new AccessDeniedException("No tienes acceso a este detalle de orden");
        }
        
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }
    
    // ✅ SEGURO: Solo admin y empleados ven estadísticas
    @GetMapping("/statistics/products-purchased")
    public ResponseEntity<List<Object[]>> getTotalProductsPurchased(
            @AuthenticationPrincipal Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(detailOrderProviderService.getTotalProductsPurchased());
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }
    
    // ✅ SEGURO: Solo admin y empleados ven estadísticas mensuales
    @GetMapping("/statistics/products-purchased/month/{month}")
    public ResponseEntity<List<Object[]>> getTotalProductsPurchasedByMonth(
            @PathVariable int month,
            @AuthenticationPrincipal Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(detailOrderProviderService.getTotalProductsPurchasedByMonth(month));
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
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
