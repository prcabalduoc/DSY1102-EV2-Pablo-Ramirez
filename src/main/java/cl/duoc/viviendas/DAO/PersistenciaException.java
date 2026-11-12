package cl.duoc.viviendas.DAO;

/**
 * Indica que no fue posible leer o guardar los archivos de viviendas.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
