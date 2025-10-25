package sgvic.excepciones;

/**
 * Excepción de acceso a datos.
 * Se lanza cuando ocurre un error con la base de datos o JDBC.
 * Ejemplo: error de conexión, constraint violada, sentencia SQL incorrecta.
 */
public class DataAccessException extends Exception {

    public DataAccessException(String mensaje) {
        super(mensaje);
    }

    public DataAccessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
