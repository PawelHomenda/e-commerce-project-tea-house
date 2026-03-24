package es.springbootcourse.auth_server.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para GlobalExceptionHandler.
 * Verifica el formato y contenido de las respuestas de error.
 */
@DisplayName("GlobalExceptionHandler - Manejo centralizado de errores")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest mockRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        mockRequest = mock(WebRequest.class);
        when(mockRequest.getDescription(false)).thenReturn("uri=/oauth2/authorize");
    }

    // =========================================================
    // OAuth2AuthenticationException
    // =========================================================

    @Nested
    @DisplayName("handleOAuth2Exception - errores OAuth2")
    class OAuth2ExceptionTests {

        @Test
        @DisplayName("Devuelve HTTP 401 Unauthorized")
        void oauth2Exception_Returns401() {
            var ex = new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "Error genérico");

            ResponseEntity<Map<String, Object>> response = handler.handleOAuth2Exception(ex, mockRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("El body contiene el campo 'status' con valor 401")
        void oauth2Exception_BodyContainsStatus401() {
            var ex = new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "error");

            Map<String, Object> body = handler.handleOAuth2Exception(ex, mockRequest).getBody();

            assertThat(body).containsEntry("status", 401);
        }

        @Test
        @DisplayName("El body contiene el campo 'error' con valor 'Unauthorized'")
        void oauth2Exception_BodyContainsErrorUnauthorized() {
            var ex = new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "error");

            Map<String, Object> body = handler.handleOAuth2Exception(ex, mockRequest).getBody();

            assertThat(body).containsEntry("error", "Unauthorized");
        }

        @Test
        @DisplayName("El body contiene el campo 'timestamp'")
        void oauth2Exception_BodyContainsTimestamp() {
            var ex = new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "error");

            Map<String, Object> body = handler.handleOAuth2Exception(ex, mockRequest).getBody();

            assertThat(body).containsKey("timestamp");
        }

        @Test
        @DisplayName("El body contiene el campo 'path' con la URI de la petición")
        void oauth2Exception_BodyContainsPath() {
            var ex = new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "error");

            Map<String, Object> body = handler.handleOAuth2Exception(ex, mockRequest).getBody();

            assertThat(body).containsEntry("path", "/oauth2/authorize");
        }

        @Test
        @DisplayName("El mensaje indica 'redirect_uri inválido' cuando el error contiene 'redirect_uri'")
        void oauth2Exception_RedirectUriMessage() {
            var ex = new OAuth2AuthenticationException(new OAuth2Error("invalid_request"),
                    "El redirect_uri no está registrado");

            Map<String, Object> body = handler.handleOAuth2Exception(ex, mockRequest).getBody();

            assertThat((String) body.get("message")).contains("redirect_uri");
        }

        @Test
        @DisplayName("El mensaje indica credenciales inválidas cuando el error contiene 'invalid_client'")
        void oauth2Exception_InvalidClientMessage() {
            var ex = new OAuth2AuthenticationException(new OAuth2Error("invalid_client"),
                    "invalid_client");

            Map<String, Object> body = handler.handleOAuth2Exception(ex, mockRequest).getBody();

            assertThat((String) body.get("message")).containsIgnoringCase("cliente");
        }

        @Test
        @DisplayName("El mensaje indica solicitud inválida cuando el error contiene 'invalid_request'")
        void oauth2Exception_InvalidRequestMessage() {
            var ex = new OAuth2AuthenticationException(new OAuth2Error("invalid_request"),
                    "invalid_request");

            Map<String, Object> body = handler.handleOAuth2Exception(ex, mockRequest).getBody();

            assertThat((String) body.get("message")).containsIgnoringCase("inválida");
        }

        @Test
        @DisplayName("El mensaje es el propio mensaje de la excepción cuando no coincide con ningún patrón")
        void oauth2Exception_GenericMessage() {
            String errorMsg = "Algún otro error OAuth2";
            var ex = new OAuth2AuthenticationException(new OAuth2Error("unknown_error"), errorMsg);

            Map<String, Object> body = handler.handleOAuth2Exception(ex, mockRequest).getBody();

            assertThat((String) body.get("message")).isEqualTo(errorMsg);
        }
    }

    // =========================================================
    // Exception genérica
    // =========================================================

    @Nested
    @DisplayName("handleGenericException - errores inesperados del servidor")
    class GenericExceptionTests {

        @Test
        @DisplayName("Devuelve HTTP 500 Internal Server Error")
        void genericException_Returns500() {
            ResponseEntity<Map<String, Object>> response =
                    handler.handleGenericException(new RuntimeException("boom"), mockRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Test
        @DisplayName("El body contiene el campo 'status' con valor 500")
        void genericException_BodyContainsStatus500() {
            Map<String, Object> body =
                    handler.handleGenericException(new RuntimeException("boom"), mockRequest).getBody();

            assertThat(body).containsEntry("status", 500);
        }

        @Test
        @DisplayName("El body contiene el campo 'error' con valor 'Internal Server Error'")
        void genericException_BodyContainsErrorMessage() {
            Map<String, Object> body =
                    handler.handleGenericException(new RuntimeException("boom"), mockRequest).getBody();

            assertThat(body).containsEntry("error", "Internal Server Error");
        }

        @Test
        @DisplayName("El body contiene un mensaje genérico de error interno")
        void genericException_BodyContainsGenericMessage() {
            Map<String, Object> body =
                    handler.handleGenericException(new RuntimeException("boom"), mockRequest).getBody();

            assertThat((String) body.get("message")).isNotBlank();
        }

        @Test
        @DisplayName("El body contiene el campo 'timestamp'")
        void genericException_BodyContainsTimestamp() {
            Map<String, Object> body =
                    handler.handleGenericException(new RuntimeException("boom"), mockRequest).getBody();

            assertThat(body).containsKey("timestamp");
        }

        @Test
        @DisplayName("El body contiene el campo 'path'")
        void genericException_BodyContainsPath() {
            Map<String, Object> body =
                    handler.handleGenericException(new RuntimeException("boom"), mockRequest).getBody();

            assertThat(body).containsEntry("path", "/oauth2/authorize");
        }

        @Test
        @DisplayName("No expone el mensaje interno de la excepción al cliente")
        void genericException_DoesNotExposeInternalMessage() {
            Map<String, Object> body =
                    handler.handleGenericException(new RuntimeException("NullPointer en línea 42"), mockRequest).getBody();

            // El mensaje visible al cliente debe ser genérico, no el mensaje real
            assertThat((String) body.get("message")).doesNotContain("NullPointer en línea 42");
        }
    }
}
