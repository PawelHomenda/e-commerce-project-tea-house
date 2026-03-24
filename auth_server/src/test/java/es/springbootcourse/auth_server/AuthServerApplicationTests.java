package es.springbootcourse.auth_server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de arranque del contexto Spring.
 * Verifica que la aplicación carga correctamente todos sus beans.
 */
@SpringBootTest
@DisplayName("AuthServerApplication - Smoke test de contexto")
class AuthServerApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("El contexto Spring se carga sin errores")
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    @DisplayName("El bean JwtTokenService está registrado en el contexto")
    void jwtTokenService_IsPresent() {
        assertThat(context.containsBean("jwtTokenService")).isTrue();
    }

    @Test
    @DisplayName("El bean RegisteredClientRepository está registrado en el contexto")
    void registeredClientRepository_IsPresent() {
        assertThat(context.containsBean("registeredClientRepository")).isTrue();
    }

    @Test
    @DisplayName("El bean UserDetailsService está registrado en el contexto")
    void userDetailsService_IsPresent() {
        assertThat(context.containsBean("userDetailsService")).isTrue();
    }

    @Test
    @DisplayName("El bean AuthorizationServerSettings está registrado en el contexto")
    void authorizationServerSettings_IsPresent() {
        assertThat(context.containsBean("authorizationServerSettings")).isTrue();
    }
}
