package es.kohchiku_bayashi.e_commerce_teahouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para autorización y autenticación.
 * Captura excepciones de token expirado, credenciales inválidas y acceso denegado.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Maneja excepciones de autenticación, incluyendo token expirado.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = "Token expirado o inválido. Por favor, inicia sesión nuevamente.";

        // Personaliza el mensaje según el tipo específico de excepción
        if (ex instanceof BadCredentialsException) {
            message = "Credenciales inválidas. Verifica tu token de acceso.";
        } else if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String cause = ex.getCause().getMessage().toLowerCase();
            if (cause.contains("expired")) {
                message = "El token ha expirado. Por favor, solicita uno nuevo en el servidor de autenticación.";
            } else if (cause.contains("invalid")) {
                message = "El token es inválido. Por favor, inicia sesión nuevamente.";
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja excepciones de acceso denegado (Access Denied).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = ex.getMessage() != null ? ex.getMessage() : "No tienes permisos para acceder a este recurso.";

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", "Access Denied");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja excepciones genéricas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Error interno del servidor. Por favor, intenta más tarde.";

        // Verifica si es un error de token
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("token")) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Error de autenticación. El token puede estar expirado o ser inválido.";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }
}
