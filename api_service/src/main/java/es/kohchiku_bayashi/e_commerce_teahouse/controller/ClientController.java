package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Client;
import es.kohchiku_bayashi.e_commerce_teahouse.service.ClientService;
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
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/admin/all")
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping
    public ResponseEntity<?> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");

        if (scopes.contains("admin")) {
            return ResponseEntity.ok(clientService.findAll());
        }

        try {
            return ResponseEntity.ok(clientService.findByOauth2Id(oauth2Id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente no encontrado \n error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Client client = clientService.findById(id);
        String oauth2Id = jwt.getClaimAsString("sub");

        if (!client.getOauth2Id().equals(oauth2Id) &&
                !jwt.getClaimAsStringList("scope").contains("admin")) {
            throw new AccessDeniedException("No tienes acceso a este cliente");
        }
        return ResponseEntity.ok(client);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {
        return ResponseEntity.ok(clientService.findByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody Client client) {
        return ResponseEntity.ok(clientService.update(id, client));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
