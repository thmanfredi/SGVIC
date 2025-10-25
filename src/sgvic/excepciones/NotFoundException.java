package sgvic.excepciones;

/**
 * Excepción para indicar que un recurso no fue encontrado.
 * Ejemplo: cliente inexistente, obligación no registrada, pago no hallado, etc.
 */
public class NotFoundException extends Exception {

    public NotFoundException(String mensaje) {
        super(mensaje);
    }
}

