package es.springbootcourse.auth_server.controllers;

import es.springbootcourse.auth_server.auth.SecurityConfig;
import es.springbootcourse.auth_server.service.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de capa web para LoginController.
 * Verifica endpoints públicos de login, logout y la redirección raíz.
 */
@WebMvcTest(controllers = LoginController.class)
@Import(SecurityConfig.class)
@DisplayName("LoginController - Endpoints de autenticación")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    // =========================================================
    // GET /login
    // =========================================================

    @Test
    @DisplayName("GET /login devuelve HTTP 200")
    void getLogin_Returns200() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /login renderiza la vista 'login'")
    void getLogin_RendersLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(view().name("login"));
    }

    // =========================================================
    // GET /logout
    // =========================================================

    @Test
    @DisplayName("GET /logout devuelve HTTP 200")
    void getLogout_Returns200() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /logout renderiza la vista 'logout'")
    void getLogout_RendersLogoutView() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(view().name("logout"));
    }

    // =========================================================
    // GET / (redirección a /login)
    // =========================================================

    @Test
    @DisplayName("GET / devuelve redirección HTTP 3xx")
    void getRoot_ReturnsRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("GET / redirige a /login")
    void getRoot_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(redirectedUrl("/login"));
    }

    // =========================================================
    // Parámetros de estado en /login
    // =========================================================

    @Test
    @DisplayName("GET /login?error=true devuelve HTTP 200 (la vista gestiona el parámetro)")
    void getLogin_WithErrorParam_Returns200() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /login?logout=true devuelve HTTP 200")
    void getLogin_WithLogoutParam_Returns200() throws Exception {
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /login?session=expired devuelve HTTP 200")
    void getLogin_WithSessionExpiredParam_Returns200() throws Exception {
        mockMvc.perform(get("/login").param("session", "expired"))
                .andExpect(status().isOk());
    }
}
