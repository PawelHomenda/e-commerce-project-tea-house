package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(ProductCategory category);
    
    List<Product> findByActive(Boolean active);
    
    List<Product> findByActiveTrue();
    
    List<Product> findByActiveFalse();
    
    List<Product> findByPriceGreaterThan(Double price);
    
    List<Product> findByPriceLessThan(Double price);
    
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategoryAndActiveTrue(ProductCategory category);
    
    @Query("SELECT p FROM Product p ORDER BY p.price ASC")
    List<Product> findAllOrderByPriceAsc();
    
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    List<Product> findAllOrderByPriceDesc();
}
