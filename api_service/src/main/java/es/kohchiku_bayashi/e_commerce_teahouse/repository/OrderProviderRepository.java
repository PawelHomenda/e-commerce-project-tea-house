package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Provider;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    Double getTotalCost();
    
    @Query("SELECT SUM(op.total) FROM OrderProvider op WHERE MONTH(op.orderDate) = :month")
    Double getTotalCostByMonth(@Param("month") int month);
    
    @Query("SELECT p.name, SUM(op.total) FROM OrderProvider op JOIN op.provider p GROUP BY p.name ORDER BY SUM(op.total)")
    List<Object[]> getTotalCostByProvider();
    
    // ✅ Nuevo: Buscar órdenes por oauth2Id del proveedor
    @Query("SELECT op FROM OrderProvider op WHERE op.provider.oauth2Id = :oauth2Id")
    List<OrderProvider> findByProviderOauth2Id(@Param("oauth2Id") String oauth2Id);
}
