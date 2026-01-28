package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

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
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void contextLoads() {
        assertNotNull(clientRepository);
        System.out.println("✅ Context loaded successfully!");
    }
}