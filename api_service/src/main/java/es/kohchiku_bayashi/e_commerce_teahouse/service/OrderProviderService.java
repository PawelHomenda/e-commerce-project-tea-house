package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.OrderProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderProviderService {
    
    private final OrderProviderRepository orderProviderRepository;
    
    public List<OrderProvider> findAll() {
        return orderProviderRepository.findAll();
    }
    
    public OrderProvider findById(Long id) {
        return orderProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));
    }
    
    // ✅ Nuevo: Buscar órdenes por oauth2Id del proveedor
    public List<OrderProvider> findByProviderOauth2Id(String oauth2Id) {
        return orderProviderRepository.findByProviderOauth2Id(oauth2Id);
    }
    
    public OrderProvider save(OrderProvider orderProvider) {
        return orderProviderRepository.save(orderProvider);
    }
    
    public OrderProvider update(Long id, OrderProvider orderProvider) {
        OrderProvider existing = findById(id);
        
        existing.setProvider(orderProvider.getProvider());
        existing.setEmployee(orderProvider.getEmployee());
        existing.setOrderDate(orderProvider.getOrderDate());
        existing.setTotal(orderProvider.getTotal());
        existing.setObservations(orderProvider.getObservations());
        
        return orderProviderRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!orderProviderRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado con id: " + id);
        }
        orderProviderRepository.deleteById(id);
    }
    
    public List<OrderProvider> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return orderProviderRepository.findByOrderDateBetween(startDate, endDate);
    }
    
    public List<OrderProvider> findByMonthAndYear(int month, int year) {
        return orderProviderRepository.findByMonthAndYear(month, year);
    }
    
    public BigDecimal getTotalCost() {
        return orderProviderRepository.getTotalCost();
    }
    
    public BigDecimal getTotalCostByMonth(int month) {
        return orderProviderRepository.getTotalCostByMonth(month);
    }

    public List<OrderProvider>  findByProviderOAuth2Id(String oauth2Id) {
        return orderProviderRepository.findByProviderOauth2Id(oauth2Id);
    }
}
