package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.OrderState;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ServiceType;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.OrderClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderClientService {
    
    private final OrderClientRepository orderClientRepository;
    
    public List<OrderClient> findAll() {
        return orderClientRepository.findAll();
    }
    
    public OrderClient findById(Long id) {
        return orderClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));
    }
    
    public OrderClient save(OrderClient orderClient) {
        return orderClientRepository.save(orderClient);
    }
    
    public OrderClient update(Long id, OrderClient orderClient) {
        OrderClient existing = findById(id);
        
        existing.setEmployee(orderClient.getEmployee());
        existing.setOrderDate(orderClient.getOrderDate());
        existing.setOrderState(orderClient.getOrderState());
        existing.setServiceType(orderClient.getServiceType());
        
        return orderClientRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!orderClientRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado con id: " + id);
        }
        orderClientRepository.deleteById(id);
    }
    
    public List<OrderClient> findByOrderState(OrderState orderState) {
        return orderClientRepository.findByOrderState(orderState);
    }
    
    public List<OrderClient> findByServiceType(ServiceType serviceType) {
        return orderClientRepository.findByServiceType(serviceType);
    }
    
    public List<OrderClient> findActiveOrders() {
        return orderClientRepository.findActiveOrders();
    }
    
    public OrderClient updateOrderState(Long id, OrderState newState) {
        OrderClient order = findById(id);
        order.setOrderState(newState);
        return orderClientRepository.save(order);
    }
    
    public List<OrderClient> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return orderClientRepository.findByOrderDateBetween(startDate, endDate);
    }
}
