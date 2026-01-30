package es.springbootcourse.auth_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el Authorization Server.
 * Convierte errores OAuth2 a respuestas más legibles con códigos HTTP apropiados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones OAuth2 (redirect_uri inválido, etc).
     * Las convierte a 401 Unauthorized en lugar de 400 Bad Request.
     */
    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleOAuth2Exception(
            OAuth2AuthenticationException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = "Error de autenticación OAuth2";
        
        // Personalizar mensaje según el tipo de error
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("redirect_uri")) {
                message = "El redirect_uri proporcionado es inválido o no está registrado.";
            } else if (ex.getMessage().contains("invalid_client")) {
                message = "Las credenciales del cliente son inválidas.";
            } else if (ex.getMessage().contains("invalid_request")) {
                message = "Solicitud OAuth2 inválida.";
            } else {
                message = ex.getMessage();
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja excepciones genéricas como fallback.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Error interno del servidor.";

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Internal Server Error");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }
}
