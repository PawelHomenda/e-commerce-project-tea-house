package es.springbootcourse.auth_server.auth;

import es.springbootcourse.auth_server.service.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para JwtAuthenticationSuccessHandler.
 * Verifica que al autenticarse correctamente se escribe el token JWT
 * en la respuesta HTTP con el Content-Type correcto.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationSuccessHandler - Respuesta tras login exitoso")
class JwtAuthenticationSuccessHandlerTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    private JwtAuthenticationSuccessHandler handler;
    private StringWriter responseBody;

    @BeforeEach
    void setUp() throws Exception {
        handler = new JwtAuthenticationSuccessHandler(jwtTokenService);
        responseBody = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    // =========================================================
    // Estructura de la respuesta
    // =========================================================

    @Test
    @DisplayName("Establece Content-Type a 'application/json'")
    void onAuthenticationSuccess_SetsJsonContentType() throws Exception {
        when(jwtTokenService.generateToken(authentication)).thenReturn("fake.jwt.token");

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).setContentType("application/json");
    }

    @Test
    @DisplayName("Escribe en el body el campo 'token' con el valor generado")
    void onAuthenticationSuccess_WritesTokenInBody() throws Exception {
        String fakeToken = "header.payload.signature";
        when(jwtTokenService.generateToken(authentication)).thenReturn(fakeToken);

        handler.onAuthenticationSuccess(request, response, authentication);

        String body = responseBody.toString();
        assertThat(body).contains("\"token\"");
        assertThat(body).contains(fakeToken);
    }

    @Test
    @DisplayName("El body tiene formato JSON válido: { \"token\": \"...\" }")
    void onAuthenticationSuccess_BodyIsValidJson() throws Exception {
        when(jwtTokenService.generateToken(authentication)).thenReturn("a.b.c");

        handler.onAuthenticationSuccess(request, response, authentication);

        String body = responseBody.toString().trim();
        assertThat(body).startsWith("{").endsWith("}");
        assertThat(body).contains("\"token\"").contains("\"a.b.c\"");
    }

    // =========================================================
    // Interacción con el servicio
    // =========================================================

    @Test
    @DisplayName("Invoca generateToken exactamente una vez con el objeto Authentication")
    void onAuthenticationSuccess_CallsGenerateTokenOnce() throws Exception {
        when(jwtTokenService.generateToken(authentication)).thenReturn("tok");

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtTokenService, times(1)).generateToken(authentication);
    }

    @Test
    @DisplayName("No redirige ni cambia el status HTTP (deja el 200 por defecto)")
    void onAuthenticationSuccess_DoesNotSendRedirect() throws Exception {
        when(jwtTokenService.generateToken(authentication)).thenReturn("tok");

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response, never()).sendRedirect(anyString());
    }

    // =========================================================
    // Construcción del handler
    // =========================================================

    @Test
    @DisplayName("El constructor acepta un JwtTokenService no nulo")
    void constructor_AcceptsNonNullService() {
        assertThat(new JwtAuthenticationSuccessHandler(jwtTokenService)).isNotNull();
    }
}
