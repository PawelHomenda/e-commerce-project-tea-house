package es.springbootcourse.auth_server.service;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import es.springbootcourse.auth_server.auth.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para JwtTokenService.
 * Verifica la generación correcta de tokens JWT con sus claims.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenService - Generación de tokens JWT")
class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() throws Exception {
        // Generar par de claves RSA para los tests
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey publicKey   = (RSAPublicKey)  keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

        NimbusJwtEncoder encoder = new NimbusJwtEncoder(jwkSource);
        jwtDecoder = OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);

        jwtTokenService = new JwtTokenService();
        // Inyectar el encoder vía reflexión (campo privado @Autowired)
        var field = JwtTokenService.class.getDeclaredField("jwtEncoder");
        field.setAccessible(true);
        field.set(jwtTokenService, encoder);
    }

    // =========================================================
    // Generación básica del token
    // =========================================================

    @Test
    @DisplayName("Genera un token no nulo y no vacío")
    void generateToken_ReturnsNonEmptyToken() {
        Authentication auth = mockAuthentication("admin", List.of("ROLE_ADMIN"));

        String token = jwtTokenService.generateToken(auth);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("El token generado tiene formato JWT válido (tres partes separadas por '.')")
    void generateToken_HasValidJwtFormat() {
        Authentication auth = mockAuthentication("admin", List.of("ROLE_ADMIN"));

        String token = jwtTokenService.generateToken(auth);
        String[] parts = token.split("\\.");

        assertThat(parts).hasSize(3);
    }

    // =========================================================
    // Claims del token
    // =========================================================

    @Test
    @DisplayName("El subject del token coincide con el nombre del usuario")
    void generateToken_SubjectMatchesUsername() {
        Authentication auth = mockAuthentication("client1", List.of("ROLE_CLIENT"));

        String token = jwtTokenService.generateToken(auth);
        var jwt = jwtDecoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo("client1");
    }

    @Test
    @DisplayName("El issuer es 'http://127.0.0.1:9000'")
    void generateToken_IssuerIsCorrect() {
        Authentication auth = mockAuthentication("admin", List.of("ROLE_ADMIN"));

        String token = jwtTokenService.generateToken(auth);
        var jwt = jwtDecoder.decode(token);

        assertThat(jwt.getIssuer()).hasToString("http://127.0.0.1:9000");
    }

    @Test
    @DisplayName("El token expira en aproximadamente 1 hora (3600 segundos)")
    void generateToken_ExpiresInOneHour() {
        Authentication auth = mockAuthentication("employee1", List.of("ROLE_EMPLOYEE"));

        String token = jwtTokenService.generateToken(auth);
        var jwt = jwtDecoder.decode(token);

        long diffSeconds = jwt.getExpiresAt().getEpochSecond() - jwt.getIssuedAt().getEpochSecond();

        assertThat(diffSeconds).isBetween(3590L, 3610L);
    }

    @Test
    @DisplayName("El claim 'scope' contiene el rol en minúsculas sin prefijo ROLE_")
    void generateToken_ScopeStripsRolePrefix() {
        Authentication auth = mockAuthentication("admin", List.of("ROLE_ADMIN"));

        String token = jwtTokenService.generateToken(auth);
        var jwt = jwtDecoder.decode(token);

        String scope = jwt.getClaim("scope");
        assertThat(scope).contains("admin").doesNotContain("ROLE_");
    }

    @Test
    @DisplayName("El claim 'scope' incluye todos los roles del usuario")
    void generateToken_ScopeIncludesAllRoles() {
        Authentication auth = mockAuthentication("multiRole",
                List.of("ROLE_ADMIN", "ROLE_EMPLOYEE"));

        String token = jwtTokenService.generateToken(auth);
        var jwt = jwtDecoder.decode(token);

        String scope = jwt.getClaim("scope");
        assertThat(scope).contains("admin").contains("employee");
    }

    @Test
    @DisplayName("El claim 'scope' para un CLIENT contiene 'client'")
    void generateToken_ClientRoleScope() {
        Authentication auth = mockAuthentication("client2", List.of("ROLE_CLIENT"));

        String token = jwtTokenService.generateToken(auth);
        var jwt = jwtDecoder.decode(token);

        String scope = jwt.getClaim("scope");
        assertThat(scope).contains("client");
    }

    @Test
    @DisplayName("El claim 'scope' para un PROVIDER contiene 'provider'")
    void generateToken_ProviderRoleScope() {
        Authentication auth = mockAuthentication("provider1", List.of("ROLE_PROVIDER"));

        String token = jwtTokenService.generateToken(auth);
        var jwt = jwtDecoder.decode(token);

        String scope = jwt.getClaim("scope");
        assertThat(scope).contains("provider");
    }

    @Test
    @DisplayName("El token tiene issuedAt no nulo")
    void generateToken_IssuedAtIsPresent() {
        Authentication auth = mockAuthentication("employee2", List.of("ROLE_EMPLOYEE"));

        String token = jwtTokenService.generateToken(auth);
        var jwt = jwtDecoder.decode(token);

        assertThat(jwt.getIssuedAt()).isNotNull();
    }

    // =========================================================
    // Helpers
    // =========================================================

    private Authentication mockAuthentication(String username, List<String> roles) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        when(auth.getAuthorities()).thenAnswer(inv -> authorities);
        return auth;
    }
}
