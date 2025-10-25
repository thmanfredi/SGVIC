package sgvic.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase abstracta que representa una obligación fiscal de un Cliente.
 * Mapea a la tabla 'obligacion' (idObligacion, idCliente, idTipo, periodo, fechaVenc, monto, estado).
 *
 * POO:
 * - Abstracción: esta clase define el "qué" común de cualquier obligación.
 * - Polimorfismo: subclases implementan calcularInteres(...) de forma distinta.
 * - Encapsulamiento: atributos privados + getters/setters.
 *
 * Reglas de dominio (alineadas con la BD):
 * - 'periodo' formato "AAAA-MM".
 * - 'monto' > 0.
 * - Unicidad (cliente, tipo, periodo) impuesta por la BD (uk_obl). Ver validación en Servicios.
 */
public abstract class Obligacion {

    private int idObligacion;                 // PK en BD
    private Cliente cliente;                   // fk_obl_cliente
    private TipoObligacion tipo;               // fk_obl_tipo
    private String periodo;                    // "AAAA-MM"
    private LocalDate fechaVenc;               // fecha de vencimiento
    private BigDecimal monto;                  // > 0
    private EstadoObligacion estado;           // PENDIENTE/VENCIDA/PAGADA

    // === Constructores ===
    public Obligacion() { }

    // Para altas (sin id)
    public Obligacion(Cliente cliente, TipoObligacion tipo, String periodo,
                      LocalDate fechaVenc, BigDecimal monto, EstadoObligacion estado) {
        this.cliente = cliente;
        this.tipo = tipo;
        this.periodo = periodo;
        this.fechaVenc = fechaVenc;
        this.monto = monto;
        this.estado = estado;
    }

    // Completo (con id) para lecturas desde BD
    public Obligacion(int idObligacion, Cliente cliente, TipoObligacion tipo, String periodo,
                      LocalDate fechaVenc, BigDecimal monto, EstadoObligacion estado) {
        this(cliente, tipo, periodo, fechaVenc, monto, estado);
        this.idObligacion = idObligacion;
    }

    // === Getters / Setters ===
    public int getIdObligacion() { return idObligacion; }
    public void setIdObligacion(int idObligacion) { this.idObligacion = idObligacion; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public TipoObligacion getTipo() { return tipo; }
    public void setTipo(TipoObligacion tipo) { this.tipo = tipo; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public LocalDate getFechaVenc() { return fechaVenc; }
    public void setFechaVenc(LocalDate fechaVenc) { this.fechaVenc = fechaVenc; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public EstadoObligacion getEstado() { return estado; }
    public void setEstado(EstadoObligacion estado) { this.estado = estado; }

    // === Lógica común ===

    /** Devuelve true si la obligación está vencida respecto a 'hoy'. */
    public boolean estaVencida(LocalDate hoy) {
        return (estado != EstadoObligacion.PAGADA) && fechaVenc != null && fechaVenc.isBefore(hoy);
    }

    /** Marca como pagada (el Service además registrará el Pago en BD). */
    public void marcarPagada() {
        this.estado = EstadoObligacion.PAGADA;
    }

    /**
     * Cálculo de interés/multa por mora.
     * Polimórfico: cada tipo de obligación puede tener su propio cálculo.
     */
    public abstract BigDecimal calcularInteres(LocalDate hoy);
}

