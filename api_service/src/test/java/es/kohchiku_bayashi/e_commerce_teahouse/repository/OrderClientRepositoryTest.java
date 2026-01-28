package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Client;
import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.OrderState;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
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
@DisplayName("Pruebas del Repositorio de Órdenes de Cliente")
class OrderClientRepositoryTest {

    @Autowired
    private OrderClientRepository orderClientRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;
    private OrderClient testOrderClient;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
                .firstName("Pedro")
                .lastName("Sánchez López")
                .email("pedro.sanchez@example.com")
                .phoneNumber("555777888")
                .address("Calle Orden 200, Madrid")
                .oauth2Id("oauth2_pedro_999")
                .oauth2Provider("Google")
                .build();

        testOrderClient = OrderClient.builder()
                .orderDate(LocalDate.now())
                .orderState(OrderState.PENDENT)
                .serviceType(ServiceType.TABLE)
                .client(testClient)
                .build();
    }

    @Test
    @DisplayName("Debe crear una nueva orden de cliente")
    void testCreateOrderClient() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);

        // Act
        OrderClient savedOrder = orderClientRepository.save(testOrderClient);

        // Assert
        assertNotNull(savedOrder.getId());
        assertEquals(OrderState.PENDENT, savedOrder.getOrderState());
        assertNotNull(savedOrder.getOrderDate());
    }

    @Test
    @DisplayName("Debe obtener una orden de cliente por ID")
    void testGetOrderClientById() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        OrderClient savedOrder = orderClientRepository.save(testOrderClient);

        // Act
        Optional<OrderClient> foundOrder = orderClientRepository.findById(savedOrder.getId());

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals("Pedro", foundOrder.get().getClient().getFirstName());
    }

    @Test
    @DisplayName("Debe actualizar el estado de una orden")
    void testUpdateOrderStatus() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        OrderClient savedOrder = orderClientRepository.save(testOrderClient);

        // Act
        savedOrder.setOrderState(OrderState.PREPARING);
        OrderClient updatedOrder = orderClientRepository.save(savedOrder);

        // Assert
        assertEquals(OrderState.PREPARING, updatedOrder.getOrderState());
    }

    @Test
    @DisplayName("Debe eliminar una orden de cliente por ID")
    void testDeleteOrderClientById() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        OrderClient savedOrder = orderClientRepository.save(testOrderClient);
        Long orderId = savedOrder.getId();

        // Act
        orderClientRepository.deleteById(orderId);

        // Assert
        Optional<OrderClient> deletedOrder = orderClientRepository.findById(orderId);
        assertFalse(deletedOrder.isPresent());
    }

    @Test
    @DisplayName("Debe eliminar una orden de cliente por entidad")
    void testDeleteOrderClient() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        OrderClient savedOrder = orderClientRepository.save(testOrderClient);

        // Act
        orderClientRepository.delete(savedOrder);

        // Assert
        Optional<OrderClient> deletedOrder = orderClientRepository.findById(savedOrder.getId());
        assertFalse(deletedOrder.isPresent());
    }

    @Test
    @DisplayName("Debe contar todas las órdenes de cliente")
    void testCountAllOrderClients() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        orderClientRepository.save(testOrderClient);

        OrderClient orderClient2 = OrderClient.builder()
                .orderDate(LocalDate.now().minusDays(2))
                .orderState(OrderState.DELIVERED)
                .serviceType(ServiceType.TAKEAWAY)
                .client(savedClient)
                .build();
        orderClientRepository.save(orderClient2);

        // Act
        long count = orderClientRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Debe verificar si existe una orden de cliente")
    void testOrderClientExists() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        OrderClient savedOrder = orderClientRepository.save(testOrderClient);

        // Act
        boolean exists = orderClientRepository.existsById(savedOrder.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Debe retornar false cuando la orden no existe")
    void testOrderClientNotExists() {
        // Act
        boolean exists = orderClientRepository.existsById(999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Debe recuperar todas las órdenes de cliente")
    void testFindAllOrderClients() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        orderClientRepository.save(testOrderClient);

        OrderClient orderClient2 = OrderClient.builder()
                .orderDate(LocalDate.now().minusDays(1))
                .orderState(OrderState.PREPARING)
                .serviceType(ServiceType.DELIVERY)
                .client(savedClient)
                .build();
        orderClientRepository.save(orderClient2);

        // Act
        var orders = orderClientRepository.findAll();

        // Assert
        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("Debe cambiar el estado de una orden a DELIVERED")
    void testChangeOrderStatusToDelivered() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        OrderClient savedOrder = orderClientRepository.save(testOrderClient);

        // Act
        savedOrder.setOrderState(OrderState.DELIVERED);
        OrderClient updatedOrder = orderClientRepository.save(savedOrder);

        // Assert
        assertEquals(OrderState.DELIVERED, updatedOrder.getOrderState());
    }

    @Test
    @DisplayName("Debe cambiar el estado de una orden a CANCELADO")
    void testChangeOrderStatusToCancelled() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        OrderClient savedOrder = orderClientRepository.save(testOrderClient);

        // Act
        savedOrder.setOrderState(OrderState.CANCELED);
        OrderClient updatedOrder = orderClientRepository.save(savedOrder);

        // Assert
        assertEquals(OrderState.CANCELED, updatedOrder.getOrderState());
    }

    @Test
    @DisplayName("Debe recuperar todas las órdenes de un cliente específico")
    void testFindOrdersByClient() {
        // Arrange
        Client savedClient = clientRepository.save(testClient);
        testOrderClient.setClient(savedClient);
        OrderClient order1 = orderClientRepository.save(testOrderClient);

        OrderClient order2 = OrderClient.builder()
                .orderDate(LocalDate.now().minusDays(5))
                .orderState(OrderState.PREPARING)
                .serviceType(ServiceType.TABLE)
                .client(savedClient)
                .build();
        orderClientRepository.save(order2);

        // Act
        var ordersForClient = orderClientRepository.findAll();

        // Assert
        assertEquals(2, ordersForClient.size());
    }
}
