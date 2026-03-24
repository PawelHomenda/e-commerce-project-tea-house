package es.springbootcourse.auth_server.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para los endpoints estándar del Authorization Server OAuth2.
 *
 * Endpoints cubiertos:
 *  - GET  /.well-known/openid-configuration
 *  - GET  /oauth2/jwks
 *  - GET  /oauth2/authorize  (flujo Authorization Code)
 *  - POST /oauth2/token      (intercambio de código por token — simulado)
 *  - GET  /actuator/health
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integración - Endpoints OAuth2 Authorization Server")
class OAuth2AuthorizationServerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // =========================================================
    // OpenID Connect Discovery
    // =========================================================

    @Nested
    @DisplayName("/.well-known/openid-configuration - Discovery endpoint")
    class OpenIdDiscoveryTests {

        @Test
        @DisplayName("Devuelve HTTP 200")
        void discovery_Returns200() throws Exception {
            mockMvc.perform(get("/.well-known/openid-configuration"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Content-Type es application/json")
        void discovery_ReturnsJson() throws Exception {
            mockMvc.perform(get("/.well-known/openid-configuration"))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Contiene el campo 'issuer'")
        void discovery_ContainsIssuer() throws Exception {
            mockMvc.perform(get("/.well-known/openid-configuration"))
                    .andExpect(jsonPath("$.issuer").exists());
        }

        @Test
        @DisplayName("Contiene el campo 'authorization_endpoint'")
        void discovery_ContainsAuthorizationEndpoint() throws Exception {
            mockMvc.perform(get("/.well-known/openid-configuration"))
                    .andExpect(jsonPath("$.authorization_endpoint").exists());
        }

        @Test
        @DisplayName("Contiene el campo 'token_endpoint'")
        void discovery_ContainsTokenEndpoint() throws Exception {
            mockMvc.perform(get("/.well-known/openid-configuration"))
                    .andExpect(jsonPath("$.token_endpoint").exists());
        }

        @Test
        @DisplayName("Contiene el campo 'jwks_uri'")
        void discovery_ContainsJwksUri() throws Exception {
            mockMvc.perform(get("/.well-known/openid-configuration"))
                    .andExpect(jsonPath("$.jwks_uri").exists());
        }

        @Test
        @DisplayName("Soporta el grant type 'authorization_code'")
        void discovery_SupportsAuthorizationCodeGrant() throws Exception {
            mockMvc.perform(get("/.well-known/openid-configuration"))
                    .andExpect(jsonPath("$.grant_types_supported",
                            hasItem("authorization_code")));
        }
    }

    // =========================================================
    // JWKS endpoint
    // =========================================================

    @Nested
    @DisplayName("/oauth2/jwks - Clave pública RSA")
    class JwksEndpointTests {

        @Test
        @DisplayName("Devuelve HTTP 200")
        void jwks_Returns200() throws Exception {
            mockMvc.perform(get("/oauth2/jwks"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Content-Type es application/json")
        void jwks_ReturnsJson() throws Exception {
            mockMvc.perform(get("/oauth2/jwks"))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Contiene el array 'keys'")
        void jwks_ContainsKeysArray() throws Exception {
            mockMvc.perform(get("/oauth2/jwks"))
                    .andExpect(jsonPath("$.keys").isArray())
                    .andExpect(jsonPath("$.keys", hasSize(greaterThan(0))));
        }

        @Test
        @DisplayName("La primera clave tiene kty='RSA'")
        void jwks_FirstKeyIsRsa() throws Exception {
            mockMvc.perform(get("/oauth2/jwks"))
                    .andExpect(jsonPath("$.keys[0].kty").value("RSA"));
        }

        @Test
        @DisplayName("La primera clave tiene el campo 'n' (módulo RSA)")
        void jwks_FirstKeyHasModulus() throws Exception {
            mockMvc.perform(get("/oauth2/jwks"))
                    .andExpect(jsonPath("$.keys[0].n").exists());
        }

        @Test
        @DisplayName("La primera clave tiene el campo 'e' (exponente RSA)")
        void jwks_FirstKeyHasExponent() throws Exception {
            mockMvc.perform(get("/oauth2/jwks"))
                    .andExpect(jsonPath("$.keys[0].e").exists());
        }
    }

    // =========================================================
    // Authorization endpoint (sin autenticación → redirige)
    // =========================================================

    @Nested
    @DisplayName("/oauth2/authorize - Inicio de flujo Authorization Code")
    class AuthorizationEndpointTests {

        @Test
        @DisplayName("Sin autenticación redirige a /login")
        void authorize_WithoutAuth_RedirectsToLogin() throws Exception {
            mockMvc.perform(get("/oauth2/authorize")
                            .param("response_type", "code")
                            .param("client_id", "client-app")
                            .param("redirect_uri", "http://127.0.0.1:8080/login/oauth2/code/client-app")
                            .param("scope", "openid"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }

        @Test
        @DisplayName("Con client_id inválido devuelve respuesta de error")
        void authorize_WithInvalidClientId_ReturnsError() throws Exception {
            mockMvc.perform(get("/oauth2/authorize")
                            .param("response_type", "code")
                            .param("client_id", "invalid-client")
                            .param("redirect_uri", "http://127.0.0.1:8080/login/oauth2/code/client-app")
                            .param("scope", "openid"))
                    // O redirige al login (no autenticado) o devuelve error 400/401
                    .andExpect(status().is(anyOf(
                            is(302), is(400), is(401)
                    )));
        }
    }

    // =========================================================
    // Token endpoint — credenciales de cliente (sin código de auth)
    // =========================================================

    @Nested
    @DisplayName("/oauth2/token - Validación de credenciales del cliente")
    class TokenEndpointTests {

        @Test
        @DisplayName("Credenciales de cliente incorrectas devuelven 401")
        void token_WithWrongClientCredentials_Returns401() throws Exception {
            mockMvc.perform(post("/oauth2/token")
                            .with(httpBasic("client-app", "wrong-secret"))
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("grant_type", "authorization_code")
                            .param("code", "invalid-code")
                            .param("redirect_uri", "http://127.0.0.1:8080/login/oauth2/code/client-app"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Sin cabecera de autorización devuelve 401")
        void token_WithoutAuthHeader_Returns401() throws Exception {
            mockMvc.perform(post("/oauth2/token")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("grant_type", "authorization_code")
                            .param("code", "fake-code")
                            .param("redirect_uri", "http://127.0.0.1:8080/login/oauth2/code/client-app"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Código de autorización inválido con credenciales correctas devuelve 400")
        void token_WithInvalidCode_Returns400() throws Exception {
            mockMvc.perform(post("/oauth2/token")
                            .with(httpBasic("client-app", "1234"))
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("grant_type", "authorization_code")
                            .param("code", "totally-fake-code")
                            .param("redirect_uri", "http://127.0.0.1:8080/login/oauth2/code/client-app"))
                    .andExpect(status().isBadRequest());
        }
    }

    // =========================================================
    // Actuator
    // =========================================================

    @Test
    @DisplayName("GET /actuator/health devuelve HTTP 200 y status UP")
    void actuator_HealthEndpoint_ReturnsUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    // =========================================================
    // Helper
    // =========================================================

    /** Matcher que acepta un valor int que coincida con cualquiera de los enteros dados */
    private static org.hamcrest.Matcher<Integer> anyOf(
            org.hamcrest.Matcher<? super Integer>... matchers) {
        return org.hamcrest.Matchers.anyOf(matchers);
    }

    private static org.hamcrest.Matcher<Integer> is(int value) {
        return org.hamcrest.Matchers.is(value);
    }
}
