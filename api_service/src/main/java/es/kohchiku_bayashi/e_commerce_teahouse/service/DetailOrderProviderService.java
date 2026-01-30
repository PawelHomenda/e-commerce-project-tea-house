package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.exception.ResourceNotFoundException;
import es.kohchiku_bayashi.e_commerce_teahouse.model.DetailOrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.DetailOrderProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DetailOrderProviderService {
    
    private final DetailOrderProviderRepository detailOrderProviderRepository;
    private final OrderProviderService orderProviderService;
    private final ProductService productService;
    
    public List<DetailOrderProvider> findAll() {
        return detailOrderProviderRepository.findAll();
    }
    
    public DetailOrderProvider findById(Long id) {
        return detailOrderProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con id: " + id));
    }
    
    public DetailOrderProvider save(DetailOrderProvider detailOrderProvider) {
        // ✅ Cargar la orden existente (obligatorio)
        if (detailOrderProvider.getOrderProvider() != null && detailOrderProvider.getOrderProvider().getId() != null) {
            detailOrderProvider.setOrderProvider(orderProviderService.findById(detailOrderProvider.getOrderProvider().getId()));
        }
        
        // ✅ Cargar el producto existente (obligatorio)
        if (detailOrderProvider.getProduct() != null && detailOrderProvider.getProduct().getId() != null) {
            detailOrderProvider.setProduct(productService.findById(detailOrderProvider.getProduct().getId()));
        }
        
        // ✅ Si unitPrice es null, cargarlo automáticamente del producto
        if (detailOrderProvider.getUnitPrice() == null && detailOrderProvider.getProduct() != null) {
            detailOrderProvider.setUnitPrice(detailOrderProvider.getProduct().getPrice());
        }
        
        DetailOrderProvider saved = detailOrderProviderRepository.save(detailOrderProvider);
        
        // ✅ RECALCULAR EL TOTAL DE LA ORDEN
        if (saved.getOrderProvider() != null) {
            saved.getOrderProvider().recalculateTotal();
            orderProviderService.save(saved.getOrderProvider());
        }
        
        return saved;
    }
    
    public DetailOrderProvider update(Long id, DetailOrderProvider detailOrderProvider) {
        DetailOrderProvider existing = findById(id);
        
        // ✅ Cargar la orden si es proporcionada
        if (detailOrderProvider.getOrderProvider() != null && detailOrderProvider.getOrderProvider().getId() != null) {
            existing.setOrderProvider(orderProviderService.findById(detailOrderProvider.getOrderProvider().getId()));
        }
        
        // ✅ Cargar el producto si es proporcionado
        if (detailOrderProvider.getProduct() != null && detailOrderProvider.getProduct().getId() != null) {
            existing.setProduct(productService.findById(detailOrderProvider.getProduct().getId()));
        }
        
        existing.setQuantity(detailOrderProvider.getQuantity());
        
        // ✅ Actualizar unitPrice solo si es proporcionado, sino cargar del producto
        if (detailOrderProvider.getUnitPrice() != null) {
            existing.setUnitPrice(detailOrderProvider.getUnitPrice());
        } else if (existing.getProduct() != null) {
            existing.setUnitPrice(existing.getProduct().getPrice());
        }
        
        // ✅ Actualizar discountPercentage si es proporcionado
        if (detailOrderProvider.getDiscountPercentage() != null) {
            existing.setDiscountPercentage(detailOrderProvider.getDiscountPercentage());
        }
        
        DetailOrderProvider updated = detailOrderProviderRepository.save(existing);
        
        // ✅ RECALCULAR EL TOTAL DE LA ORDEN
        if (updated.getOrderProvider() != null) {
            updated.getOrderProvider().recalculateTotal();
            orderProviderService.save(updated.getOrderProvider());
        }
        
        return updated;
    }
    
    public void deleteById(Long id) {
        if (!detailOrderProviderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Detalle no encontrado con id: " + id);
        }
        
        // ✅ Obtener el detalle antes de eliminarlo para acceder a su orden
        DetailOrderProvider detail = findById(id);
        OrderProvider order = detail.getOrderProvider();
        
        // ✅ Eliminar el detalle
        detailOrderProviderRepository.deleteById(id);
        
        // ✅ RECALCULAR EL TOTAL DE LA ORDEN
        if (order != null) {
            order.recalculateTotal();
            orderProviderService.save(order);
        }
    }
    
    public List<Object[]> getTotalProductsPurchased() {
        return detailOrderProviderRepository.getTotalProductsPurchased();
    }
    
    public List<Object[]> getTotalProductsPurchasedByMonth(int month) {
        return detailOrderProviderRepository.getTotalProductsPurchasedByMonth(month);
    }

    public List<DetailOrderProvider> findByProviderOAuth2Id(String oAuth2Id) {
        return detailOrderProviderRepository.findByProviderOAuth2Id(oAuth2Id);
    }

    public List<DetailOrderProvider> findByEmployeeOAuth2Id(String oAuth2Id) {
        return detailOrderProviderRepository.findByEmployeeOAuth2Id(oAuth2Id);
    }
}
