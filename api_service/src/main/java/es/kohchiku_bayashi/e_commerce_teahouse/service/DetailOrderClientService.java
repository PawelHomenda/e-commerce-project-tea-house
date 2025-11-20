package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.DetailOrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.DetailOrderClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DetailOrderClientService {
    
    private final DetailOrderClientRepository detailOrderClientRepository;
    
    public List<DetailOrderClient> findAll() {
        return detailOrderClientRepository.findAll();
    }
    
    public DetailOrderClient findById(Long id) {
        return detailOrderClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado con id: " + id));
    }
    
    public DetailOrderClient save(DetailOrderClient detailOrderClient) {
        return detailOrderClientRepository.save(detailOrderClient);
    }
    
    public DetailOrderClient update(Long id, DetailOrderClient detailOrderClient) {
        DetailOrderClient existing = findById(id);
        
        existing.setOrderClient(detailOrderClient.getOrderClient());
        existing.setProduct(detailOrderClient.getProduct());
        existing.setQuantity(detailOrderClient.getQuantity());
        existing.setUnitPrice(detailOrderClient.getUnitPrice());
        
        return detailOrderClientRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!detailOrderClientRepository.existsById(id)) {
            throw new RuntimeException("Detalle no encontrado con id: " + id);
        }
        detailOrderClientRepository.deleteById(id);
    }
    
    public List<Object[]> getTop5ProductsByRevenue() {
        return detailOrderClientRepository.getTop5ProductsByRevenue();
    }
    
    public List<Object[]> getTop5ProductsByQuantity() {
        return detailOrderClientRepository.getTop5ProductsByQuantity();
    }
    
    public List<Object[]> getProductCountByServiceType() {
        return detailOrderClientRepository.getProductCountByServiceType();
    }
    
    public List<Product> findProductsWithoutSales() {
        return detailOrderClientRepository.findProductsWithoutSales();
    }
}
