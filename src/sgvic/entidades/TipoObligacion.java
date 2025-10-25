package sgvic.entidades;

/**
 * Representa un tipo de obligación fiscal.
 * Mapea a la tabla 'tipoobligacion' (idTipo, codigo, descripcion, periodicidad).
 *
 * Ejemplos de datos (según tu BD):
 *  - IVA  → Mensual
 *  - GAN  → Anual
 *  - MON  → Mensual
 *  - SIC  → Mensual
 *  - IIBB → Mensual
 *
 * Esta clase ilustra:
 *  - Encapsulamiento (atributos privados)
 *  - Uso de enum interno para restringir valores (abstracción)
 *  - Constructores sobrecargados
 */
public class TipoObligacion {

    // === Atributos ===
    private int idTipo;                 // PK en BD
    private String codigo;              // Ej: "IVA", "GAN"
    private String descripcion;         // Detalle del tributo
    private Periodicidad periodicidad;  // Enum (Mensual, Anual, Otra)

    // === Enum interno (ABSTRACCIÓN de periodicidad) ===
    public enum Periodicidad {
        MENSUAL, ANUAL, OTRA
    }

    // === Constructores ===
    public TipoObligacion() {
        // vacío para frameworks/DAO
    }

    public TipoObligacion(String codigo, String descripcion, Periodicidad periodicidad) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.periodicidad = periodicidad;
    }

    public TipoObligacion(int idTipo, String codigo, String descripcion, Periodicidad periodicidad) {
        this.idTipo = idTipo;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.periodicidad = periodicidad;
    }

    // === Getters y Setters ===
    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Periodicidad getPeriodicidad() { return periodicidad; }
    public void setPeriodicidad(Periodicidad periodicidad) { this.periodicidad = periodicidad; }

    // === toString para depuración y menús ===
    @Override
    public String toString() {
        return codigo + " - " + descripcion + " (" + periodicidad + ")";
    }
}
