package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.exception.ResourceNotFoundException;
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
    private final OrderClientService orderClientService;
    private final ProductService productService;
    
    public List<DetailOrderClient> findAll() {
        return detailOrderClientRepository.findAll();
    }
    
    public DetailOrderClient findById(Long id) {
        return detailOrderClientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con id: " + id));
    }
    
    public DetailOrderClient save(DetailOrderClient detailOrderClient) {
        // ✅ Cargar la orden existente (obligatorio)
        if (detailOrderClient.getOrderClient() != null && detailOrderClient.getOrderClient().getId() != null) {
            detailOrderClient.setOrderClient(orderClientService.findById(detailOrderClient.getOrderClient().getId()));
        }
        
        // ✅ Cargar el producto existente (obligatorio)
        if (detailOrderClient.getProduct() != null && detailOrderClient.getProduct().getId() != null) {
            detailOrderClient.setProduct(productService.findById(detailOrderClient.getProduct().getId()));
        }
        
        // ✅ Si unitPrice es null, cargarlo automáticamente del producto
        if (detailOrderClient.getUnitPrice() == null && detailOrderClient.getProduct() != null) {
            detailOrderClient.setUnitPrice(detailOrderClient.getProduct().getPrice());
        }
        
        return detailOrderClientRepository.save(detailOrderClient);
    }
    
    public DetailOrderClient update(Long id, DetailOrderClient detailOrderClient) {
        DetailOrderClient existing = findById(id);
        
        // ✅ Cargar la orden si es proporcionada
        if (detailOrderClient.getOrderClient() != null && detailOrderClient.getOrderClient().getId() != null) {
            existing.setOrderClient(orderClientService.findById(detailOrderClient.getOrderClient().getId()));
        }
        
        // ✅ Cargar el producto si es proporcionado
        if (detailOrderClient.getProduct() != null && detailOrderClient.getProduct().getId() != null) {
            existing.setProduct(productService.findById(detailOrderClient.getProduct().getId()));
        }
        
        existing.setQuantity(detailOrderClient.getQuantity());
        
        // ✅ Actualizar unitPrice solo si es proporcionado, sino cargar del producto
        if (detailOrderClient.getUnitPrice() != null) {
            existing.setUnitPrice(detailOrderClient.getUnitPrice());
        } else if (existing.getProduct() != null) {
            existing.setUnitPrice(existing.getProduct().getPrice());
        }
        
        // ✅ Actualizar discountPercentage si es proporcionado
        if (detailOrderClient.getDiscountPercentage() != null) {
            existing.setDiscountPercentage(detailOrderClient.getDiscountPercentage());
        }
        
        return detailOrderClientRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!detailOrderClientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Detalle no encontrado con id: " + id);
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

    public List<DetailOrderClient> findByClientOAuth2Id(String oAuth2Id) {
        return detailOrderClientRepository.findByClientOAuth2Id(oAuth2Id);
    }
}
