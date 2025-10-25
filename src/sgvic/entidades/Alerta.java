package sgvic.entidades;

import java.time.LocalDate;

/**
 * Representa una alerta por vencimiento próximo o vencido.
 * Mapea a la tabla 'alerta' (idAlerta, idObligacion, fecha, leida).
 *
 * En el sistema:
 * - Generadas automáticamente por el Servicio de Alertas.
 * - Se pueden marcar como leídas desde el menú.
 */
public class Alerta {

    private int idAlerta;           // PK en BD
    private Obligacion obligacion;  // FK en BD
    private LocalDate fecha;        // Fecha en que se generó la alerta
    private boolean leida;          // true = ya vista

    // === Constructores ===
    public Alerta() { }

    public Alerta(Obligacion obligacion, LocalDate fecha, boolean leida) {
        this.obligacion = obligacion;
        this.fecha = fecha;
        this.leida = leida;
    }

    public Alerta(int idAlerta, Obligacion obligacion, LocalDate fecha, boolean leida) {
        this(obligacion, fecha, leida);
        this.idAlerta = idAlerta;
    }

    // === Getters/Setters ===
    public int getIdAlerta() { return idAlerta; }
    public void setIdAlerta(int idAlerta) { this.idAlerta = idAlerta; }

    public Obligacion getObligacion() { return obligacion; }
    public void setObligacion(Obligacion obligacion) { this.obligacion = obligacion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }

    // === Lógica simple de dominio ===
    public void marcarLeida() {
        this.leida = true;
    }

    @Override
    public String toString() {
        return "Alerta{id=" + idAlerta +
                ", fecha=" + fecha +
                ", leida=" + leida +
                ", obligacion=" + (obligacion != null ? obligacion.getIdObligacion() : "null") +
                '}';
    }
}

