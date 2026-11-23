package co.edu.sena.resigest.exception;

/**
 * Excepcion no verificada para errores de acceso a datos (JDBC).
 * Envuelve la SQLException para no propagar detalles tecnicos a la vista.
 *
 * @author Edwar Stiven Diaz Torres
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
