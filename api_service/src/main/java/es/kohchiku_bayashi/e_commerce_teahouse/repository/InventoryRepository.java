package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Inventory;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByProduct(Product product);
    
    Optional<Inventory> findByProductId(Long productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.currentQuantity < i.minimumQuantity")
    List<Inventory> findLowStockProducts();
    
    @Query("SELECT i FROM Inventory i JOIN FETCH i.product p WHERE i.currentQuantity < i.minimumQuantity")
    List<Inventory> findLowStockProductsWithDetails();
}
