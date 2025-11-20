package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.DetailOrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailOrderProviderRepository extends JpaRepository<DetailOrderProvider, Long> {
    
    List<DetailOrderProvider> findByOrderProvider(OrderProvider orderProvider);
    
    List<DetailOrderProvider> findByProduct(Product product);
    
    @Query("SELECT p.name, SUM(dop.quantity) FROM DetailOrderProvider dop JOIN dop.product p GROUP BY p.name ORDER BY SUM(dop.quantity) DESC")
    List<Object[]> getTotalProductsPurchased();
    
    @Query("SELECT p.name, SUM(dop.quantity) FROM DetailOrderProvider dop JOIN dop.product p JOIN dop.orderProvider op WHERE MONTH(op.orderDate) = :month GROUP BY p.name ORDER BY SUM(dop.quantity) DESC")
    List<Object[]> getTotalProductsPurchasedByMonth(@Param("month") int month);
}
