package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import es.kohchiku_bayashi.e_commerce_teahouse.service.EmployeeService;
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
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    // ✅ SEGURO: Solo ADMIN ve todos
    @GetMapping("/admin/all")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.findAll());
    }
    
    // ✅ SEGURO: Empleado ve sus propios datos, ADMIN ve todos
    @GetMapping
    public ResponseEntity<?> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {
        String oauth2Id = jwt.getClaimAsString("sub");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        
        // Si es admin, devuelve todos los empleados
        if (scopes.contains("admin")) {
            return ResponseEntity.ok(employeeService.findAll());
        }
        
        // Si no es admin, devuelve solo su perfil
        try {
            return ResponseEntity.ok(employeeService.findByOauth2Id(oauth2Id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Empleado no encontrado \n error: " + e.getMessage());
        }
    }
    
    // El Empleado solo ve sus datos o ADMIN ve cualquiera
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            Employee employee = employeeService.findById(id);
            String oauth2Id = jwt.getClaimAsString("sub");
            
            if (!employee.getOauth2Id().equals(oauth2Id) && 
                !jwt.getClaimAsStringList("scope").contains("admin")) {
                throw new AccessDeniedException("No tienes acceso a este empleado");
            }
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Employee> getEmployeeByEmail(@PathVariable String email) {
        return ResponseEntity.ok(employeeService.findByEmail(email));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchByFullName(@RequestParam String fullName) {
        return ResponseEntity.ok(employeeService.findByFullName(fullName));
    }
    
    @GetMapping("/salary-range")
    public ResponseEntity<List<Employee>> getEmployeesBySalaryRange(
            @RequestParam Double minSalary,
            @RequestParam Double maxSalary) {
        return ResponseEntity.ok(employeeService.findBySalaryRange(minSalary, maxSalary));
    }
    
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.save(employee));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.update(id, employee));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
