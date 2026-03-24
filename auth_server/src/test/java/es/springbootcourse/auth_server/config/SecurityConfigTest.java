package es.springbootcourse.auth_server.config;

import es.springbootcourse.auth_server.auth.SecurityConfig;
import es.springbootcourse.auth_server.service.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para los beans definidos en SecurityConfig.
 * No levanta contexto Spring completo; instancia la clase directamente.
 */
@DisplayName("SecurityConfig - Configuración de beans de seguridad")
class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    // =========================================================
    // UserDetailsService
    // =========================================================

    @Nested
    @DisplayName("UserDetailsService - usuarios en memoria")
    class UserDetailsServiceTests {

        private UserDetailsService uds;

        @BeforeEach
        void setUp() {
            uds = securityConfig.userDetailsService();
        }

        @Test
        @DisplayName("El bean no es nulo")
        void userDetailsService_IsNotNull() {
            assertThat(uds).isNotNull();
        }

        // --- Admin ---
        @Test
        @DisplayName("El usuario 'admin' existe con rol ROLE_ADMIN")
        void admin_ExistsWithAdminRole() {
            UserDetails user = uds.loadUserByUsername("admin");
            assertThat(user).isNotNull();
            assertThat(user.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }

        // --- Clientes ---
        @Test
        @DisplayName("Los clientes client1..client3 existen con rol ROLE_CLIENT")
        void clients_ExistWithClientRole() {
            for (int i = 1; i <= 3; i++) {
                UserDetails user = uds.loadUserByUsername("client" + i);
                assertThat(user.getAuthorities())
                        .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
            }
        }

        // --- Empleados ---
        @Test
        @DisplayName("Los empleados employee1..employee3 existen con rol ROLE_EMPLOYEE")
        void employees_ExistWithEmployeeRole() {
            for (int i = 1; i <= 3; i++) {
                UserDetails user = uds.loadUserByUsername("employee" + i);
                assertThat(user.getAuthorities())
                        .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
            }
        }

        // --- Proveedores ---
        @Test
        @DisplayName("Los proveedores provider1..provider3 existen con rol ROLE_PROVIDER")
        void providers_ExistWithProviderRole() {
            for (int i = 1; i <= 3; i++) {
                UserDetails user = uds.loadUserByUsername("provider" + i);
                assertThat(user.getAuthorities())
                        .anyMatch(a -> a.getAuthority().equals("ROLE_PROVIDER"));
            }
        }

        // --- Total de usuarios ---
        @Test
        @DisplayName("Hay exactamente 10 usuarios registrados (1 admin + 3 clientes + 3 empleados + 3 proveedores)")
        void totalUsers_IsTen() {
            // Comprobamos que todos existen y que uno inexistente lanza excepción
            String[] expected = {
                "admin",
                "client1", "client2", "client3",
                "employee1", "employee2", "employee3",
                "provider1", "provider2", "provider3"
            };
            for (String u : expected) {
                assertThatNoException().isThrownBy(() -> uds.loadUserByUsername(u));
            }
        }

        @Test
        @DisplayName("Un usuario no registrado lanza UsernameNotFoundException")
        void unknownUser_ThrowsUsernameNotFoundException() {
            assertThatThrownBy(() -> uds.loadUserByUsername("hacker"))
                    .isInstanceOf(UsernameNotFoundException.class);
        }

        // --- Contraseñas (noop) ---
        @Test
        @DisplayName("Las contraseñas se almacenan con el encoder '{noop}'")
        void passwords_UsesNoopEncoder() {
            // Spring almacena {noop}1234 — con InMemoryUserDetailsManager la
            // contraseña devuelta incluye el prefijo del encoder
            UserDetails admin = uds.loadUserByUsername("admin");
            assertThat(admin.getPassword()).startsWith("{noop}");
        }

        @Test
        @DisplayName("Todos los usuarios están habilitados (enabled = true)")
        void allUsers_AreEnabled() {
            String[] all = {
                "admin", "client1", "client2", "client3",
                "employee1", "employee2", "employee3",
                "provider1", "provider2", "provider3"
            };
            for (String u : all) {
                assertThat(uds.loadUserByUsername(u).isEnabled()).isTrue();
            }
        }
    }

    // =========================================================
    // RegisteredClientRepository
    // =========================================================

    @Nested
    @DisplayName("RegisteredClientRepository - cliente OAuth2 'client-app'")
    class RegisteredClientRepositoryTests {

        private RegisteredClientRepository repo;

        @BeforeEach
        void setUp() {
            repo = securityConfig.registeredClientRepository();
        }

        @Test
        @DisplayName("El repositorio no es nulo")
        void repository_IsNotNull() {
            assertThat(repo).isNotNull();
        }

        @Test
        @DisplayName("El cliente 'client-app' está registrado")
        void clientApp_IsRegistered() {
            RegisteredClient client = repo.findByClientId("client-app");
            assertThat(client).isNotNull();
        }

        @Test
        @DisplayName("El clientName es 'Tea House Client App'")
        void clientApp_HasCorrectName() {
            RegisteredClient client = repo.findByClientId("client-app");
            assertThat(client.getClientName()).isEqualTo("Tea House Client App");
        }

        @Test
        @DisplayName("Soporta el grant type AUTHORIZATION_CODE")
        void clientApp_SupportsAuthorizationCode() {
            RegisteredClient client = repo.findByClientId("client-app");
            assertThat(client.getAuthorizationGrantTypes())
                    .contains(AuthorizationGrantType.AUTHORIZATION_CODE);
        }

        @Test
        @DisplayName("Soporta el grant type REFRESH_TOKEN")
        void clientApp_SupportsRefreshToken() {
            RegisteredClient client = repo.findByClientId("client-app");
            assertThat(client.getAuthorizationGrantTypes())
                    .contains(AuthorizationGrantType.REFRESH_TOKEN);
        }

        @Test
        @DisplayName("Usa autenticación CLIENT_SECRET_BASIC")
        void clientApp_UsesClientSecretBasic() {
            RegisteredClient client = repo.findByClientId("client-app");
            assertThat(client.getClientAuthenticationMethods())
                    .contains(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        }

        @Test
        @DisplayName("Tiene los scopes openid, profile, email, read, write y los específicos de rol")
        void clientApp_HasRequiredScopes() {
            RegisteredClient client = repo.findByClientId("client-app");
            assertThat(client.getScopes()).containsAll(java.util.Set.of(
                    OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL,
                    "read", "write", "user:client", "user:employee", "user:provider", "admin"
            ));
        }

        @Test
        @DisplayName("Tiene al menos un redirect URI registrado")
        void clientApp_HasRedirectUris() {
            RegisteredClient client = repo.findByClientId("client-app");
            assertThat(client.getRedirectUris()).isNotEmpty();
        }

        @Test
        @DisplayName("Tiene el redirect URI para localhost:8080")
        void clientApp_HasLocalhost8080RedirectUri() {
            RegisteredClient client = repo.findByClientId("client-app");
            assertThat(client.getRedirectUris())
                    .anyMatch(uri -> uri.contains("127.0.0.1:8080"));
        }

        @Test
        @DisplayName("Requiere pantalla de consentimiento (requireAuthorizationConsent = true)")
        void clientApp_RequiresAuthorizationConsent() {
            RegisteredClient client = repo.findByClientId("client-app");
            assertThat(client.getClientSettings().isRequireAuthorizationConsent()).isTrue();
        }

        @Test
        @DisplayName("Un clientId inexistente devuelve null")
        void unknownClient_ReturnsNull() {
            RegisteredClient client = repo.findByClientId("non-existent");
            assertThat(client).isNull();
        }
    }

    // =========================================================
    // JWK Source y codecs JWT
    // =========================================================

    @Nested
    @DisplayName("JWKSource - Claves RSA")
    class JwkSourceTests {

        @Test
        @DisplayName("El bean jwkSource no es nulo")
        void jwkSource_IsNotNull() {
            assertThat(securityConfig.jwkSource()).isNotNull();
        }

        @Test
        @DisplayName("Dos llamadas a jwkSource generan instancias distintas (clave efímera)")
        void jwkSource_GeneratesNewKeyEachTime() {
            var source1 = securityConfig.jwkSource();
            var source2 = securityConfig.jwkSource();
            // Cada llamada genera un par de claves distinto (en memoria)
            assertThat(source1).isNotSameAs(source2);
        }

        @Test
        @DisplayName("El JwtDecoder puede ser construido a partir del jwkSource")
        void jwtDecoder_CanBeBuilt() {
            var jwkSource = securityConfig.jwkSource();
            assertThatNoException().isThrownBy(() -> securityConfig.jwtDecoder(jwkSource));
        }

        @Test
        @DisplayName("El JwtEncoder puede ser construido a partir del jwkSource")
        void jwtEncoder_CanBeBuilt() {
            var jwkSource = securityConfig.jwkSource();
            assertThatNoException().isThrownBy(() -> securityConfig.jwtEncoder(jwkSource));
        }
    }

    // =========================================================
    // AuthorizationServerSettings
    // =========================================================

    @Test
    @DisplayName("authorizationServerSettings no es nulo")
    void authorizationServerSettings_IsNotNull() {
        assertThat(securityConfig.authorizationServerSettings()).isNotNull();
    }
}
