package es.kohchiku_bayashi.e_commerce_teahouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
     * Maneja excepciones de integridad de datos (conflictos de eliminación).
     */
    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityException(
            DataIntegrityException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        String message = ex.getMessage() != null ? ex.getMessage() : "No se puede completar la operación debido a referencias de datos.";

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Conflict");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja excepciones de recurso duplicado (email, oauth2Id existente, etc).
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResourceException(
            DuplicateResourceException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        String message = ex.getMessage() != null ? ex.getMessage() : "El recurso ya existe.";

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Conflict");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja excepciones de violación de integridad de BD (Duplicate key, constraints, etc).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        String message = "No se puede completar la operación. Verifica los datos enviados.";
        
        // Personalizar mensajes según el tipo de error
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Duplicate entry") || ex.getMessage().contains("oauth2Id")) {
                message = "El oauth2Id especificado ya existe. Usa un oauth2Id único.";
            } else if (ex.getMessage().contains("email")) {
                message = "El email especificado ya existe. Usa un email único.";
            } else if (ex.getMessage().contains("Duplicate")) {
                message = "Los datos enviados contienen duplicados. Verifica la unicidad de los campos.";
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Conflict");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja errores de validación (Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        // Recopilar todos los errores de validación
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Bad Request");
        body.put("message", "Error de validación en los datos enviados");
        body.put("details", errors);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja excepciones de validación de restricciones JPA (persist time validation).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        // Recopilar todos los errores de validación
        String errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Bad Request");
        body.put("message", "Error de validación en los datos enviados");
        body.put("details", errors);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja excepciones de recurso no encontrado.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage() != null ? ex.getMessage() : "Recurso no encontrado.";

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Not Found");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    /**
     * Maneja errores de deserialización JSON (enums inválidos, tipos incorrectos, etc).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Error en el formato JSON enviado";

        // Personalizamos el mensaje según el tipo de error
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Cannot coerce empty String")) {
                message = "Campo obligatorio vacío. Verifica que todos los campos requeridos tengan valores válidos.";
            } else if (ex.getMessage().contains("enum")) {
                message = "Valor de enumeración inválido. Verifica que hayas usado uno de los valores permitidos.";
            } else if (ex.getMessage().contains("JSON parse")) {
                message = "JSON malformado. Verifica la estructura de tu solicitud.";
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Bad Request");
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
