package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Employee;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.OrderState;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderClientRepository extends JpaRepository<OrderClient, Long> {
    
    List<OrderClient> findByEmployee(Employee employee);
    
    List<OrderClient> findByOrderState(OrderState orderState);
    
    List<OrderClient> findByServiceType(ServiceType serviceType);
    
    List<OrderClient> findByOrderDate(LocalDate orderDate);
    
    List<OrderClient> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT oc FROM OrderClient oc WHERE oc.orderState = :state AND oc.serviceType = :type")
    List<OrderClient> findByStateAndServiceType(@Param("state") OrderState state, @Param("type") ServiceType serviceType);
    
    @Query("SELECT oc FROM OrderClient oc WHERE oc.orderState IN ('PENDIENTE', 'EN_PREPARACION')")
    List<OrderClient> findActiveOrders();
    
    // ✅ Nuevo: Buscar pedidos por oauth2Id del cliente
    @Query("SELECT oc FROM OrderClient oc WHERE oc.client.oauth2Id = :oauth2Id")
    List<OrderClient> findByClientOauth2Id(@Param("oauth2Id") String oauth2Id);
}