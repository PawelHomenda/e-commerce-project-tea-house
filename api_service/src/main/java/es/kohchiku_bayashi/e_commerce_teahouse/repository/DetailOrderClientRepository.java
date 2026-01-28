package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.DetailOrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailOrderClientRepository extends JpaRepository<DetailOrderClient, Long> {
    
    List<DetailOrderClient> findByOrderClient(OrderClient orderClient);
    
    List<DetailOrderClient> findByProduct(Product product);
    
    @Query("SELECT p.name, SUM(doc.quantity * doc.unitPrice) FROM DetailOrderClient doc JOIN doc.product p GROUP BY p.name ORDER BY SUM(doc.quantity * doc.unitPrice) DESC")
    List<Object[]> getTop5ProductsByRevenue();
    
    @Query("SELECT p.name, SUM(doc.quantity) FROM DetailOrderClient doc JOIN doc.product p GROUP BY p.name ORDER BY SUM(doc.quantity) DESC")
    List<Object[]> getTop5ProductsByQuantity();
    
    @Query("SELECT oc.serviceType, SUM(doc.quantity) FROM DetailOrderClient doc JOIN doc.orderClient oc GROUP BY oc.serviceType")
    List<Object[]> getProductCountByServiceType();
    
    @Query("SELECT p FROM Product p WHERE p.id NOT IN (SELECT DISTINCT doc.product.id FROM DetailOrderClient doc)")
    List<Product> findProductsWithoutSales();

    @Query("SELECT doc FROM DetailOrderClient doc JOIN doc.orderClient oc JOIN oc.client c WHERE c.oauth2Id = :oAuth2Id")
    List<DetailOrderClient> findByClientOAuth2Id(String oAuth2Id);
}
