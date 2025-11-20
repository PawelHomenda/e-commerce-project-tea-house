package es.kohchiku_bayashi.e_commerce_teahouse.controller;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import es.kohchiku_bayashi.e_commerce_teahouse.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
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
