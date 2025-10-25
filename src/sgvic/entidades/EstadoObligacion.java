package sgvic.entidades;

/**
 * Estado de la obligación fiscal.
 * Mapea al ENUM de la columna 'estado' en la tabla 'obligacion'.
 * Valores posibles en BD: 'Pendiente', 'Vencida', 'Pagada'.
 */
public enum EstadoObligacion {
    PENDIENTE, VENCIDA, PAGADA
}

