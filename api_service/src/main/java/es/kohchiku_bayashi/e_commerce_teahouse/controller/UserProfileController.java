package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Client;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Provider;
import es.kohchiku_bayashi.e_commerce_teahouse.service.ClientService;
import es.kohchiku_bayashi.e_commerce_teahouse.service.EmployeeService;
import es.kohchiku_bayashi.e_commerce_teahouse.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final ProviderService providerService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        String sub = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", sub);
        profile.put("isActive", true);
        profile.put("createdAt", jwt.getIssuedAt());

        // Determinar rol y buscar entidad correspondiente
        if (scopes != null && scopes.contains("admin")) {
            profile.put("role", "ADMIN");
            profile.put("firstName", "Administrador");
            profile.put("lastName", "");
            profile.put("email", sub);
            return ResponseEntity.ok(profile);
        }

        if (scopes != null && scopes.contains("client")) {
            try {
                Client client = clientService.findByOauth2Id(sub);
                profile.put("role", "CLIENT");
                profile.put("firstName", client.getFirstName());
                profile.put("lastName", client.getLastName());
                profile.put("email", client.getEmail());
                profile.put("phone", client.getPhoneNumber());
                profile.put("address", client.getAddress());
                profile.put("id", client.getId());
                return ResponseEntity.ok(profile);
            } catch (RuntimeException e) {
                profile.put("role", "CLIENT");
                profile.put("firstName", sub);
                profile.put("lastName", "");
                profile.put("email", "");
                return ResponseEntity.ok(profile);
            }
        }

        if (scopes != null && scopes.contains("employee")) {
            try {
                Employee employee = employeeService.findByOauth2Id(sub);
                profile.put("role", "EMPLOYEE");
                profile.put("firstName", employee.getFirstName());
                profile.put("lastName", employee.getLastName());
                profile.put("email", employee.getEmail());
                profile.put("phone", employee.getPhoneNumber());
                profile.put("salary", employee.getSalary());
                profile.put("id", employee.getId());
                return ResponseEntity.ok(profile);
            } catch (RuntimeException e) {
                profile.put("role", "EMPLOYEE");
                profile.put("firstName", sub);
                profile.put("lastName", "");
                profile.put("email", "");
                return ResponseEntity.ok(profile);
            }
        }

        if (scopes != null && scopes.contains("provider")) {
            try {
                Provider provider = providerService.findByOauth2Id(sub);
                profile.put("role", "PROVIDER");
                profile.put("firstName", provider.getName());
                profile.put("lastName", "");
                profile.put("email", provider.getEmail());
                profile.put("phone", provider.getPhoneNumber());
                profile.put("address", provider.getAddress());
                profile.put("id", provider.getId());
                return ResponseEntity.ok(profile);
            } catch (RuntimeException e) {
                profile.put("role", "PROVIDER");
                profile.put("firstName", sub);
                profile.put("lastName", "");
                profile.put("email", "");
                return ResponseEntity.ok(profile);
            }
        }

        // Fallback: rol desconocido
        profile.put("role", "USER");
        profile.put("firstName", sub);
        profile.put("lastName", "");
        profile.put("email", sub);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> updates) {

        String sub = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");

        // Admin no tiene entidad en BD, no se puede editar
        if (scopes != null && scopes.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "El perfil de administrador no se puede modificar desde aquí"));
        }

        String firstName = (String) updates.get("firstName");
        String lastName = (String) updates.get("lastName");
        String phone = (String) updates.get("phone");

        if (scopes != null && scopes.contains("client")) {
            Client client = clientService.findByOauth2Id(sub);
            if (firstName != null) client.setFirstName(firstName);
            if (lastName != null) client.setLastName(lastName);
            if (phone != null) client.setPhoneNumber(phone);
            Client updated = clientService.update(client.getId(), client);

            Map<String, Object> profile = buildProfileMap(sub, "CLIENT", updated.getFirstName(),
                    updated.getLastName(), updated.getEmail(), updated.getPhoneNumber(), updated.getId());
            profile.put("address", updated.getAddress());
            return ResponseEntity.ok(profile);
        }

        if (scopes != null && scopes.contains("employee")) {
            Employee employee = employeeService.findByOauth2Id(sub);
            if (firstName != null) employee.setFirstName(firstName);
            if (lastName != null) employee.setLastName(lastName);
            if (phone != null) employee.setPhoneNumber(phone);
            Employee updated = employeeService.update(employee.getId(), employee);

            Map<String, Object> profile = buildProfileMap(sub, "EMPLOYEE", updated.getFirstName(),
                    updated.getLastName(), updated.getEmail(), updated.getPhoneNumber(), updated.getId());
            profile.put("salary", updated.getSalary());
            return ResponseEntity.ok(profile);
        }

        if (scopes != null && scopes.contains("provider")) {
            Provider provider = providerService.findByOauth2Id(sub);
            if (firstName != null) provider.setName(firstName);
            if (phone != null) provider.setPhoneNumber(phone);
            Provider updated = providerService.update(provider.getId(), provider);

            Map<String, Object> profile = buildProfileMap(sub, "PROVIDER", updated.getName(),
                    "", updated.getEmail(), updated.getPhoneNumber(), updated.getId());
            profile.put("address", updated.getAddress());
            return ResponseEntity.ok(profile);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "No se pudo determinar el tipo de usuario"));
    }

    private Map<String, Object> buildProfileMap(String username, String role,
            String firstName, String lastName, String email, String phone, Long id) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("username", username);
        profile.put("role", role);
        profile.put("firstName", firstName);
        profile.put("lastName", lastName);
        profile.put("email", email);
        profile.put("phone", phone);
        profile.put("id", id);
        profile.put("isActive", true);
        return profile;
    }
}
