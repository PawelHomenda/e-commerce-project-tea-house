package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    Optional<Employee> findByOauth2Id(String oauth2Id);
    
    boolean existsByEmail(String email);
    
    List<Employee> findByFirstNameContainingIgnoreCase(String firstName);
    
    List<Employee> findByLastNameContainingIgnoreCase(String lastName);
    
    @Query("SELECT e FROM Employee e WHERE CONCAT(e.firstName, ' ', e.lastName) LIKE %?1%")
    List<Employee> findByFullNameContaining(String fullName);
    
    List<Employee> findBySalaryGreaterThan(Double salary);
    
    List<Employee> findBySalaryLessThan(Double salary);
    
    List<Employee> findBySalaryBetween(Double minSalary, Double maxSalary);
}
