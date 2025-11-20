package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Provider;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderProviderRepository extends JpaRepository<OrderProvider, Long> {
    
    List<OrderProvider> findByProvider(Provider provider);
    
    List<OrderProvider> findByEmployee(Employee employee);
    
    List<OrderProvider> findByOrderDate(LocalDate orderDate);
    
    List<OrderProvider> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT op FROM OrderProvider op WHERE MONTH(op.orderDate) = :month AND YEAR(op.orderDate) = :year")
    List<OrderProvider> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
    
    @Query("SELECT SUM(op.total) FROM OrderProvider op")
    BigDecimal getTotalCost();
    
    @Query("SELECT SUM(op.total) FROM OrderProvider op WHERE MONTH(op.orderDate) = :month")
    BigDecimal getTotalCostByMonth(@Param("month") int month);
    
    @Query("SELECT p.name, SUM(op.total) FROM OrderProvider op JOIN op.provider p GROUP BY p.name ORDER BY SUM(op.total)")
    List<Object[]> getTotalCostByProvider();
}
