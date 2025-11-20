package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Provider;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProviderService {
    
    private final ProviderRepository providerRepository;
    
    public List<Provider> findAll() {
        return providerRepository.findAll();
    }
    
    public Provider findById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + id));
    }
    
    public Provider findByEmail(String email) {
        return providerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con email: " + email));
    }
    
    public Provider save(Provider provider) {
        if (providerRepository.existsByEmail(provider.getEmail())) {
            throw new RuntimeException("Ya existe un proveedor con el email: " + provider.getEmail());
        }
        return providerRepository.save(provider);
    }
    
    public Provider update(Long id, Provider provider) {
        Provider existing = findById(id);
        
        if (!existing.getEmail().equals(provider.getEmail()) && 
            providerRepository.existsByEmail(provider.getEmail())) {
            throw new RuntimeException("Ya existe un proveedor con el email: " + provider.getEmail());
        }
        
        existing.setName(provider.getName());
        existing.setContact(provider.getContact());
        existing.setPhoneNumber(provider.getPhoneNumber());
        existing.setEmail(provider.getEmail());
        existing.setAddress(provider.getAddress());
        
        return providerRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new RuntimeException("Proveedor no encontrado con id: " + id);
        }
        providerRepository.deleteById(id);
    }
    
    public List<Provider> findByName(String name) {
        return providerRepository.findByNameContainingIgnoreCase(name);
    }
}
