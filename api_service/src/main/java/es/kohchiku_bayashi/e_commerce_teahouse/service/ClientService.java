package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.exception.ResourceNotFoundException;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Client;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    
    private final ClientRepository clientRepository;
    
    public List<Client> findAll() {
        return clientRepository.findAll();
    }
    
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }
    
    public Client findByOauth2Id(String oauth2Id) {
        return clientRepository.findByOauth2Id(oauth2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con oauth2Id: " + oauth2Id));
    }
    
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con email: " + email));
    }
    
    public Client save(Client client) {
        return clientRepository.save(client);
    }
    
    public Client update(Long id, Client client) {
        Client existing = findById(id);
        
        existing.setFirstName(client.getFirstName());
        existing.setLastName(client.getLastName());
        existing.setEmail(client.getEmail());
        existing.setPhoneNumber(client.getPhoneNumber());
        existing.setAddress(client.getAddress());
        
        return clientRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        clientRepository.deleteById(id);
    }
    
    // ✅ Obtener o crear cliente desde OAuth2
    public Client getOrCreateClientFromOAuth2(String oauth2Id, String email, String firstName, String lastName, String provider) {
        return clientRepository.findByOauth2Id(oauth2Id)
                .orElseGet(() -> {
                    Client newClient = Client.builder()
                            .oauth2Id(oauth2Id)
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .oauth2Provider(provider)
                            .build();
                    return clientRepository.save(newClient);
                });
    }
}
