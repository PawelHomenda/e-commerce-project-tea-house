package es.springbootcourse.auth_server.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para las cadenas de filtros de seguridad.
 * Levanta el contexto completo de Spring Boot (puerto random / H2 en memoria).
 *
 * Cubre:
 *  - Acceso libre a /login, /logout, /actuator/health
 *  - Protección de rutas privadas
 *  - Flujo de login por formulario (credenciales correctas / incorrectas)
 *  - Logout e invalidación de sesión
 *  - Respuesta JWT al autenticarse con éxito
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integración - Cadenas de filtros de seguridad")
class SecurityFilterChainIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // =========================================================
    // Rutas públicas (sin autenticación)
    // =========================================================

    @Nested
    @DisplayName("Rutas públicas - accesibles sin autenticar")
    class PublicEndpointsTests {

        @Test
        @DisplayName("GET /login es accesible sin autenticación (200)")
        void loginPage_IsPublic() throws Exception {
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /actuator/health es accesible sin autenticación (200)")
        void actuatorHealth_IsPublic() throws Exception {
            mockMvc.perform(get("/actuator/health"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET / redirige sin autenticación (3xx)")
        void root_RedirectsWithoutAuth() throws Exception {
            mockMvc.perform(get("/"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    // =========================================================
    // Rutas protegidas (requieren autenticación)
    // =========================================================

    @Nested
    @DisplayName("Rutas protegidas - redirigen a /login si no autenticado")
    class ProtectedEndpointsTests {

        @Test
        @DisplayName("GET /oauth2/authorize sin autenticación redirige a /login")
        void oauth2Authorize_RedirectsToLogin() throws Exception {
            mockMvc.perform(get("/oauth2/authorize")
                            .param("response_type", "code")
                            .param("client_id", "client-app")
                            .param("redirect_uri", "http://127.0.0.1:8080/login/oauth2/code/client-app")
                            .param("scope", "openid"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }

    // =========================================================
    // Flujo de login por formulario
    // =========================================================

    @Nested
    @DisplayName("Formulario de login - credenciales")
    class FormLoginTests {

        @Test
        @DisplayName("POST /login con credenciales correctas devuelve 200 con token JWT")
        void login_WithValidCredentials_ReturnsJwtToken() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "admin")
                            .param("password", "1234")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.token").isString())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("POST /login con credenciales de un cliente devuelve token JWT")
        void login_WithClientCredentials_ReturnsJwtToken() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "client1")
                            .param("password", "1234")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("POST /login con credenciales de un empleado devuelve token JWT")
        void login_WithEmployeeCredentials_ReturnsJwtToken() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "employee1")
                            .param("password", "1234")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("POST /login con credenciales de un proveedor devuelve token JWT")
        void login_WithProviderCredentials_ReturnsJwtToken() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "provider1")
                            .param("password", "1234")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("POST /login con contraseña incorrecta redirige a /login?error=true")
        void login_WithWrongPassword_RedirectsToError() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "admin")
                            .param("password", "wrong")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?error=true"));
        }

        @Test
        @DisplayName("POST /login con usuario inexistente redirige a /login?error=true")
        void login_WithUnknownUser_RedirectsToError() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "nobody")
                            .param("password", "1234")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?error=true"));
        }

        @Test
        @DisplayName("POST /login sin token CSRF devuelve 403 Forbidden")
        void login_WithoutCsrfToken_Returns403() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "admin")
                            .param("password", "1234"))
                    // Sin csrf() → el filtro CSRF rechaza la petición
                    .andExpect(status().isForbidden());
        }
    }

    // =========================================================
    // Logout
    // =========================================================

    @Nested
    @DisplayName("Logout - cierre de sesión")
    class LogoutTests {

        @Test
        @DisplayName("POST /logout redirige a /login?logout=true")
        @WithMockUser(username = "admin", roles = "ADMIN")
        void logout_RedirectsToLoginWithLogoutParam() throws Exception {
            mockMvc.perform(post("/logout").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?logout=true"));
        }
    }

    // =========================================================
    // Contenido del token JWT
    // =========================================================

    @Nested
    @DisplayName("Token JWT - claims de la respuesta")
    class JwtTokenClaimsTests {

        @Test
        @DisplayName("El token del admin contiene el scope 'admin'")
        void adminToken_ContainsAdminScope() throws Exception {
            String responseBody = mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "admin")
                            .param("password", "1234")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // El scope 'admin' debe estar codificado en el payload del JWT
            // Decodificamos solo el payload (parte central del JWT)
            String[] parts = extractToken(responseBody).split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            org.assertj.core.api.Assertions.assertThat(payload).contains("admin");
        }

        @Test
        @DisplayName("El token del client1 contiene el scope 'client'")
        void clientToken_ContainsClientScope() throws Exception {
            String responseBody = mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username", "client1")
                            .param("password", "1234")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String payload = new String(java.util.Base64.getUrlDecoder()
                    .decode(extractToken(responseBody).split("\\.")[1]));
            org.assertj.core.api.Assertions.assertThat(payload).contains("client");
        }

        /** Extrae el valor del campo "token" del JSON {"token":"..."} */
        private String extractToken(String json) {
            // JSON simple: {"token":"<valor>"}
            return json.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
        }
    }
}
