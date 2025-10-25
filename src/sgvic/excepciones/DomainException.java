package sgvic.excepciones;

/**
 * Excepción de dominio.
 * Se usa para representar errores de reglas de negocio.
 * Ejemplo: CUIT duplicado, período inválido, monto <= 0, etc.
 */
public class DomainException extends Exception {

    public DomainException(String mensaje) {
        super(mensaje);
    }

    public DomainException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
