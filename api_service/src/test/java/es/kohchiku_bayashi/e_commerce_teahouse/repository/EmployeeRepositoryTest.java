package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
    "spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.sql.init.mode=never"
})
@DisplayName("Pruebas del Repositorio de Empleados")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = Employee.builder()
                .firstName("Roberto")
                .lastName("Fernández Ruiz")
                .salary(2500.00)
                .phoneNumber("555888999")
                .email("roberto.fernandez@teahouse.com")
                .oauth2Id("oauth2_employee_001")
                .build();
    }

    @Test
    @DisplayName("Debe crear un nuevo empleado correctamente")
    void testCreateEmployee() {
        // Act
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Assert
        assertNotNull(savedEmployee.getId());
        assertEquals("Roberto", savedEmployee.getFirstName());
        assertEquals("Fernández Ruiz", savedEmployee.getLastName());
        assertEquals(2500.00, savedEmployee.getSalary());
        assertEquals("555888999", savedEmployee.getPhoneNumber());
    }

    @Test
    @DisplayName("Debe obtener un empleado por ID")
    void testGetEmployeeById() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Act
        Optional<Employee> foundEmployee = employeeRepository.findById(savedEmployee.getId());

        // Assert
        assertTrue(foundEmployee.isPresent());
        assertEquals("Roberto", foundEmployee.get().getFirstName());
        assertEquals("roberto.fernandez@teahouse.com", foundEmployee.get().getEmail());
    }

    @Test
    @DisplayName("Debe actualizar un empleado existente")
    void testUpdateEmployee() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Act
        savedEmployee.setFirstName("Ramón");
        savedEmployee.setSalary(2750.00);
        savedEmployee.setPhoneNumber("666999000");
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        // Assert
        assertEquals("Ramón", updatedEmployee.getFirstName());
        assertEquals(2750.00, updatedEmployee.getSalary());
        assertEquals("666999000", updatedEmployee.getPhoneNumber());
    }

    @Test
    @DisplayName("Debe eliminar un empleado por ID")
    void testDeleteEmployeeById() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);
        Long employeeId = savedEmployee.getId();

        // Act
        employeeRepository.deleteById(employeeId);

        // Assert
        Optional<Employee> deletedEmployee = employeeRepository.findById(employeeId);
        assertFalse(deletedEmployee.isPresent());
    }

    @Test
    @DisplayName("Debe eliminar un empleado por entidad")
    void testDeleteEmployee() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Act
        employeeRepository.delete(savedEmployee);

        // Assert
        Optional<Employee> deletedEmployee = employeeRepository.findById(savedEmployee.getId());
        assertFalse(deletedEmployee.isPresent());
    }

    @Test
    @DisplayName("Debe contar todos los empleados")
    void testCountAllEmployees() {
        // Arrange
        employeeRepository.save(testEmployee);
        Employee employee2 = Employee.builder()
                .firstName("Adriana")
                .lastName("Martínez García")
                .salary(2200.00)
                .phoneNumber("777111222")
                .email("adriana.martinez@teahouse.com")
                .oauth2Id("oauth2_employee_002")
                .build();
        employeeRepository.save(employee2);

        // Act
        long count = employeeRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Debe verificar si existe un empleado")
    void testEmployeeExists() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Act
        boolean exists = employeeRepository.existsById(savedEmployee.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Debe retornar false cuando el empleado no existe")
    void testEmployeeNotExists() {
        // Act
        boolean exists = employeeRepository.existsById(999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Debe recuperar todos los empleados")
    void testFindAllEmployees() {
        // Arrange
        employeeRepository.save(testEmployee);
        Employee employee2 = Employee.builder()
                .firstName("Sofía")
                .lastName("López Díaz")
                .salary(2400.00)
                .phoneNumber("888222333")
                .email("sofia.lopez@teahouse.com")
                .oauth2Id("oauth2_employee_003")
                .build();
        employeeRepository.save(employee2);

        // Act
        var employees = employeeRepository.findAll();

        // Assert
        assertEquals(2, employees.size());
    }

    @Test
    @DisplayName("Debe actualizar solo el salario de un empleado")
    void testPartialUpdateEmployee() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);
        Double newSalary = 3000.00;

        // Act
        savedEmployee.setSalary(newSalary);
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        // Assert
        assertEquals(newSalary, updatedEmployee.getSalary());
        assertEquals("Roberto", updatedEmployee.getFirstName());
    }

    @Test
    @DisplayName("Debe actualizar el email de un empleado")
    void testUpdateEmployeeEmail() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);
        String newEmail = "roberto.nuevo@teahouse.com";

        // Act
        savedEmployee.setEmail(newEmail);
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        // Assert
        assertEquals(newEmail, updatedEmployee.getEmail());
    }

    @Test
    @DisplayName("Debe validar unicidad del email del empleado")
    void testEmployeeEmailUniqueness() {
        // Arrange
        employeeRepository.save(testEmployee);
        Employee duplicateEmailEmployee = Employee.builder()
                .firstName("Otro")
                .lastName("Empleado")
                .salary(2300.00)
                .phoneNumber("999333444")
                .email("roberto.fernandez@teahouse.com")  // Email duplicado
                .oauth2Id("oauth2_employee_004")
                .build();

        // Act & Assert
        assertThrows(Exception.class, () -> {
            employeeRepository.save(duplicateEmailEmployee);
            employeeRepository.flush();
        });
    }

    @Test
    @DisplayName("Debe validar unicidad del oauth2Id del empleado")
    void testEmployeeOAuth2IdUniqueness() {
        // Arrange
        employeeRepository.save(testEmployee);
        Employee duplicateOAuth2Employee = Employee.builder()
                .firstName("Otro")
                .lastName("Usuario")
                .salary(2300.00)
                .phoneNumber("999333444")
                .email("otro.email@teahouse.com")
                .oauth2Id("oauth2_employee_001")  // oauth2Id duplicado
                .build();

        // Act & Assert
        assertThrows(Exception.class, () -> {
            employeeRepository.save(duplicateOAuth2Employee);
            employeeRepository.flush();
        });
    }

    @Test
    @DisplayName("Debe aumentar el salario de un empleado")
    void testIncreaseEmployeeSalary() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);
        Double currentSalary = savedEmployee.getSalary();

        // Act
        Double newSalary = currentSalary + 500;
        savedEmployee.setSalary(newSalary);
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        // Assert
        assertEquals(3000.00, updatedEmployee.getSalary());
    }

    @Test
    @DisplayName("Debe actualizar el número de teléfono de un empleado")
    void testUpdateEmployeePhoneNumber() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);
        String newPhoneNumber = "777888999";

        // Act
        savedEmployee.setPhoneNumber(newPhoneNumber);
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        // Assert
        assertEquals(newPhoneNumber, updatedEmployee.getPhoneNumber());
    }
}
