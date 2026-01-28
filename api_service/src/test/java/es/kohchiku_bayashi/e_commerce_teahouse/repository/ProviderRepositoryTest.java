package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
    "spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.sql.init.mode=never"
})
@DisplayName("Pruebas del Repositorio de Proveedores")
class ProviderRepositoryTest {

    @Autowired
    private ProviderRepository providerRepository;

    private Provider testProvider;

    @BeforeEach
    void setUp() {
        testProvider = Provider.builder()
                .name("Proveedores de Té Premium S.A.")
                .contact("Luis García")
                .email("contacto@proveedorpremium.com")
                .phoneNumber("555111222")
                .address("Calle Industrial 100, Granada")
                .oauth2Id("oauth2_provider_001")
                .oauth2Provider("Google")
                .build();
    }

    @Test
    @DisplayName("Debe crear un nuevo proveedor correctamente")
    void testCreateProvider() {
        // Act
        Provider savedProvider = providerRepository.save(testProvider);

        // Assert
        assertNotNull(savedProvider.getId());
        assertEquals("Proveedores de Té Premium S.A.", savedProvider.getName());
        assertEquals("Luis García", savedProvider.getContact());
        assertEquals("contacto@proveedorpremium.com", savedProvider.getEmail());
    }

    @Test
    @DisplayName("Debe obtener un proveedor por ID")
    void testGetProviderById() {
        // Arrange
        Provider savedProvider = providerRepository.save(testProvider);

        // Act
        Optional<Provider> foundProvider = providerRepository.findById(savedProvider.getId());

        // Assert
        assertTrue(foundProvider.isPresent());
        assertEquals("Proveedores de Té Premium S.A.", foundProvider.get().getName());
        assertEquals("555111222", foundProvider.get().getPhoneNumber());
    }

    @Test
    @DisplayName("Debe actualizar un proveedor existente")
    void testUpdateProvider() {
        // Arrange
        Provider savedProvider = providerRepository.save(testProvider);

        // Act
        savedProvider.setName("Proveedores de Té Orgánico Plus");
        savedProvider.setContact("Carlos Martínez");
        savedProvider.setPhoneNumber("666222333");
        Provider updatedProvider = providerRepository.save(savedProvider);

        // Assert
        assertEquals("Proveedores de Té Orgánico Plus", updatedProvider.getName());
        assertEquals("Carlos Martínez", updatedProvider.getContact());
        assertEquals("666222333", updatedProvider.getPhoneNumber());
    }

    @Test
    @DisplayName("Debe eliminar un proveedor por ID")
    void testDeleteProviderById() {
        // Arrange
        Provider savedProvider = providerRepository.save(testProvider);
        Long providerId = savedProvider.getId();

        // Act
        providerRepository.deleteById(providerId);

        // Assert
        Optional<Provider> deletedProvider = providerRepository.findById(providerId);
        assertFalse(deletedProvider.isPresent());
    }

    @Test
    @DisplayName("Debe eliminar un proveedor por entidad")
    void testDeleteProvider() {
        // Arrange
        Provider savedProvider = providerRepository.save(testProvider);

        // Act
        providerRepository.delete(savedProvider);

        // Assert
        Optional<Provider> deletedProvider = providerRepository.findById(savedProvider.getId());
        assertFalse(deletedProvider.isPresent());
    }

    @Test
    @DisplayName("Debe contar todos los proveedores")
    void testCountAllProviders() {
        // Arrange
        providerRepository.save(testProvider);
        Provider provider2 = Provider.builder()
                .name("Importadores de Té del Oriente")
                .contact("María López")
                .email("info@teedeloriente.com")
                .phoneNumber("777333444")
                .address("Calle Comercial 250, Barcelona")
                .oauth2Id("oauth2_provider_002")
                .oauth2Provider("GitHub")
                .build();
        providerRepository.save(provider2);

        // Act
        long count = providerRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Debe verificar si existe un proveedor")
    void testProviderExists() {
        // Arrange
        Provider savedProvider = providerRepository.save(testProvider);

        // Act
        boolean exists = providerRepository.existsById(savedProvider.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Debe retornar false cuando el proveedor no existe")
    void testProviderNotExists() {
        // Act
        boolean exists = providerRepository.existsById(999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Debe recuperar todos los proveedores")
    void testFindAllProviders() {
        // Arrange
        providerRepository.save(testProvider);
        Provider provider2 = Provider.builder()
                .name("Té Exótico Internacional")
                .contact("Juan Rodríguez")
                .email("ventas@teexotico.com")
                .phoneNumber("888444555")
                .address("Avenida Principal 500, Valencia")
                .oauth2Id("oauth2_provider_003")
                .oauth2Provider("Google")
                .build();
        providerRepository.save(provider2);

        // Act
        var providers = providerRepository.findAll();

        // Assert
        assertEquals(2, providers.size());
    }

    @Test
    @DisplayName("Debe actualizar solo el email de un proveedor")
    void testPartialUpdateProvider() {
        // Arrange
        Provider savedProvider = providerRepository.save(testProvider);
        String newEmail = "newemail@proveedorpremium.com";

        // Act
        savedProvider.setEmail(newEmail);
        Provider updatedProvider = providerRepository.save(savedProvider);

        // Assert
        assertEquals(newEmail, updatedProvider.getEmail());
        assertEquals("Proveedores de Té Premium S.A.", updatedProvider.getName());
    }

    @Test
    @DisplayName("Debe actualizar la dirección de un proveedor")
    void testUpdateProviderAddress() {
        // Arrange
        Provider savedProvider = providerRepository.save(testProvider);
        String newAddress = "Calle Nueva 750, Málaga";

        // Act
        savedProvider.setAddress(newAddress);
        Provider updatedProvider = providerRepository.save(savedProvider);

        // Assert
        assertEquals(newAddress, updatedProvider.getAddress());
    }

    @Test
    @DisplayName("Debe actualizar la persona de contacto")
    void testUpdateContact() {
        // Arrange
        Provider savedProvider = providerRepository.save(testProvider);
        String newContact = "Pedro Sánchez";

        // Act
        savedProvider.setContact(newContact);
        Provider updatedProvider = providerRepository.save(savedProvider);

        // Assert
        assertEquals(newContact, updatedProvider.getContact());
    }

    @Test
    @DisplayName("Debe validar unicidad del email del proveedor")
    void testProviderEmailUniqueness() {
        // Arrange
        providerRepository.save(testProvider);
        Provider duplicateEmailProvider = Provider.builder()
                .name("Otro Proveedor")
                .contact("Otro Contacto")
                .email("contacto@proveedorpremium.com")  // Email duplicado
                .phoneNumber("999888777")
                .oauth2Id("oauth2_provider_004")
                .build();

        // Act & Assert
        assertThrows(Exception.class, () -> {
            providerRepository.save(duplicateEmailProvider);
            providerRepository.flush();
        });
    }
}
