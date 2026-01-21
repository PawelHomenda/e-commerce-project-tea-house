package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }
    
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con id: " + id));
    }
    
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con email: " + email));
    }
    
    // ✅ Nuevo: Buscar por oauth2Id
    public Employee findByOauth2Id(String oauth2Id) {
        return employeeRepository.findByOauth2Id(oauth2Id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con oauth2Id: " + oauth2Id));
    }
    
    public Employee save(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Ya existe un empleado con el email: " + employee.getEmail());
        }
        return employeeRepository.save(employee);
    }
    
    public Employee update(Long id, Employee employee) {
        Employee existing = findById(id);
        
        if (!existing.getEmail().equals(employee.getEmail()) && 
            employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Ya existe un empleado con el email: " + employee.getEmail());
        }
        
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setSalary(employee.getSalary());
        existing.setPhoneNumber(employee.getPhoneNumber());
        existing.setEmail(employee.getEmail());
        
        return employeeRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Empleado no encontrado con id: " + id);
        }
        employeeRepository.deleteById(id);
    }
    
    public List<Employee> findByFullName(String fullName) {
        return employeeRepository.findByFullNameContaining(fullName);
    }
    
    public List<Employee> findBySalaryRange(Double minSalary, Double maxSalary) {
        return employeeRepository.findBySalaryBetween(minSalary, maxSalary);
    }
    
    // ✅ Auto-crear empleado desde OAuth2
    public Employee getOrCreateEmployeeFromOAuth2(String oauth2Id, String email, String firstName, String lastName, String provider) {
        return employeeRepository.findByOauth2Id(oauth2Id)
                .orElseGet(() -> {
                    Employee newEmployee = Employee.builder()
                            .oauth2Id(oauth2Id)
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .oauth2Provider(provider)
                            .salary(0.0) // Default salary, debe ser actualizado
                            .phoneNumber("0000000000") // Default, debe ser actualizado
                            .build();
                    return employeeRepository.save(newEmployee);
                });
    }
}
