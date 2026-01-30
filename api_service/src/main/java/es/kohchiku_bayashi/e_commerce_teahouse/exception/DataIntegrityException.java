package es.kohchiku_bayashi.e_commerce_teahouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando hay una violación de integridad de datos,
 * como cuando se intenta eliminar un recurso que tiene referencias en otros registros.
 */
@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class DataIntegrityException extends RuntimeException {
    
    public DataIntegrityException(String message) {
        super(message);
    }
    
    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
