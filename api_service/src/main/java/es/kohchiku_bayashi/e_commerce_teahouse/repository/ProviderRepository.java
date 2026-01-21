package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    
    Optional<Provider> findByEmail(String email);
    
    Optional<Provider> findByOauth2Id(String oauth2Id);
    
    boolean existsByEmail(String email);
    
    List<Provider> findByNameContainingIgnoreCase(String name);
    
    List<Provider> findByContactContainingIgnoreCase(String contact);
}
