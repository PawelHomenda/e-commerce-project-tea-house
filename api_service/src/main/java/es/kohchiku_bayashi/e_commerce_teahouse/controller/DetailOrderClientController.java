package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.DetailOrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import es.kohchiku_bayashi.e_commerce_teahouse.service.DetailOrderClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/details/orders/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DetailOrderClientController {
    
    private final DetailOrderClientService detailOrderClientService;
    

    @GetMapping("/admin/all")
    public ResponseEntity<List<DetailOrderClient>> getAllDetails() {
            return ResponseEntity.ok(detailOrderClientService.findAll());
    }

    @GetMapping
    public ResponseEntity<List<DetailOrderClient>> getMyDetails(@AuthenticationPrincipal Jwt jwt) {
        String OAuth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");

        if (scopes.contains("admin") || scopes.contains("user:employee")){
            return ResponseEntity.ok(detailOrderClientService.findAll());
        }
        else if(scopes.contains("user:client")){
            return ResponseEntity.ok(detailOrderClientService.findByClientOAuth2Id(OAuth2Id));
        }
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
    }
    
    // ✅ SEGURO: Validar propiedad del detalle de orden
    @GetMapping("/{id}")
    public ResponseEntity<DetailOrderClient> getDetailById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        DetailOrderClient detail = detailOrderClientService.findById(id);
        
        // Admin/Employee ven todo
        if (scopes.contains("admin") || scopes.contains("user:employee")) {
            return ResponseEntity.ok(detail);
        }
        
        // Cliente solo ve detalles de sus propias órdenes
        if (scopes.contains("user:client")) {
            if (detail.getOrderClient().getClient().getOauth2Id().equals(oauth2Id)) {
                return ResponseEntity.ok(detail);
            }
            throw new AccessDeniedException("No tienes acceso a este detalle de orden");
        }
        
        throw new AccessDeniedException("No tienes permisos para acceder a este recurso.");
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
