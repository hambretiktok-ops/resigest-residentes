package co.edu.sena.resigest.exception;

/**
 * Excepcion para datos invalidos ingresados por el usuario
 * (campos vacios, correo mal escrito, documento repetido, etc.).
 *
 * @author Edwar Stiven Diaz Torres
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
