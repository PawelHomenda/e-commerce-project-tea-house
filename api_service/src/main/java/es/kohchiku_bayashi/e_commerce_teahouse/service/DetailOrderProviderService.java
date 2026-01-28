package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.DetailOrderProvider;
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
    
    public List<DetailOrderProvider> findAll() {
        return detailOrderProviderRepository.findAll();
    }
    
    public DetailOrderProvider findById(Long id) {
        return detailOrderProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado con id: " + id));
    }
    
    public DetailOrderProvider save(DetailOrderProvider detailOrderProvider) {
        return detailOrderProviderRepository.save(detailOrderProvider);
    }
    
    public DetailOrderProvider update(Long id, DetailOrderProvider detailOrderProvider) {
        DetailOrderProvider existing = findById(id);
        
        existing.setOrderProvider(detailOrderProvider.getOrderProvider());
        existing.setProduct(detailOrderProvider.getProduct());
        existing.setQuantity(detailOrderProvider.getQuantity());
        existing.setUnitPrice(detailOrderProvider.getUnitPrice());
        
        return detailOrderProviderRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!detailOrderProviderRepository.existsById(id)) {
            throw new RuntimeException("Detalle no encontrado con id: " + id);
        }
        detailOrderProviderRepository.deleteById(id);
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
