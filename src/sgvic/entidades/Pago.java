package sgvic.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa un pago realizado por una obligación fiscal.
 * Mapea a la tabla 'pago' (idPago, idObligacion, fecha, medio, monto).
 *
 * En el sistema:
 * - Cada obligación puede tener a lo sumo un pago (1:1).
 * - Se registra a través del Servicio de Obligaciones al marcar como pagada.
 */
public class Pago {

    private int idPago;             // PK en BD
    private Obligacion obligacion;  // FK en BD (1:1)
    private LocalDate fecha;        // Fecha de pago
    private String medio;           // Ej: "Transferencia", "Efectivo"
    private BigDecimal monto;       // Monto abonado

    // === Constructores ===
    public Pago() { }

    public Pago(Obligacion obligacion, LocalDate fecha, String medio, BigDecimal monto) {
        this.obligacion = obligacion;
        this.fecha = fecha;
        this.medio = medio;
        this.monto = monto;
    }

    public Pago(int idPago, Obligacion obligacion, LocalDate fecha, String medio, BigDecimal monto) {
        this(obligacion, fecha, medio, monto);
        this.idPago = idPago;
    }

    // === Getters/Setters ===
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public Obligacion getObligacion() { return obligacion; }
    public void setObligacion(Obligacion obligacion) { this.obligacion = obligacion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getMedio() { return medio; }
    public void setMedio(String medio) { this.medio = medio; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    @Override
    public String toString() {
        return "Pago{id=" + idPago +
                ", fecha=" + fecha +
                ", medio='" + medio + '\'' +
                ", monto=" + monto + '}';
    }
}


