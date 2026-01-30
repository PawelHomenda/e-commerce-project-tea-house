package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.exception.ResourceNotFoundException;
import es.kohchiku_bayashi.e_commerce_teahouse.exception.DataIntegrityException;
import es.kohchiku_bayashi.e_commerce_teahouse.exception.DuplicateResourceException;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.EmployeeRepository;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.OrderClientRepository;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.OrderProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final OrderClientRepository orderClientRepository;
    private final OrderProviderRepository orderProviderRepository;
    
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }
    
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));
    }
    
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con email: " + email));
    }
    
    // ✅ Nuevo: Buscar por oauth2Id
    public Employee findByOauth2Id(String oauth2Id) {
        return employeeRepository.findByOauth2Id(oauth2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con oauth2Id: " + oauth2Id));
    }
    
    public Employee save(Employee employee) {
        // ✅ Verificar si el email ya existe
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateResourceException("Ya existe un empleado con el email: " + employee.getEmail());
        }
        
        // ✅ Verificar si el oauth2Id ya existe
        if (employeeRepository.findByOauth2Id(employee.getOauth2Id()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un empleado con el oauth2Id: " + employee.getOauth2Id());
        }
        
        return employeeRepository.save(employee);
    }
    
    public Employee update(Long id, Employee employee) {
        Employee existing = findById(id);
        
        // ✅ Verificar si el nuevo email ya existe (y es diferente al actual)
        if (!existing.getEmail().equals(employee.getEmail()) && 
            employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateResourceException("Ya existe un empleado con el email: " + employee.getEmail());
        }
        
        // ✅ Verificar si el nuevo oauth2Id ya existe (y es diferente al actual)
        if (!existing.getOauth2Id().equals(employee.getOauth2Id()) &&
            employeeRepository.findByOauth2Id(employee.getOauth2Id()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un empleado con el oauth2Id: " + employee.getOauth2Id());
        }
        
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setSalary(employee.getSalary());
        existing.setPhoneNumber(employee.getPhoneNumber());
        existing.setEmail(employee.getEmail());
        existing.setOauth2Id(employee.getOauth2Id());
        existing.setOauth2Provider(employee.getOauth2Provider());
        
        return employeeRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        Employee employee = findById(id);
        
        // ✅ Verificar si el empleado tiene órdenes de clientes asociadas
        List<?> clientOrders = orderClientRepository.findByEmployee(employee);
        if (!clientOrders.isEmpty()) {
            throw new DataIntegrityException(
                "No se puede eliminar el empleado. Tiene " + clientOrders.size() + 
                " orden(es) de cliente(s) asociada(s). Asigne estas órdenes a otro empleado primero."
            );
        }
        
        // ✅ Verificar si el empleado tiene órdenes de proveedores asociadas
        List<?> providerOrders = orderProviderRepository.findByEmployee(employee);
        if (!providerOrders.isEmpty()) {
            throw new DataIntegrityException(
                "No se puede eliminar el empleado. Tiene " + providerOrders.size() + 
                " orden(es) de proveedor(es) asociada(s). Asigne estas órdenes a otro empleado primero."
            );
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
