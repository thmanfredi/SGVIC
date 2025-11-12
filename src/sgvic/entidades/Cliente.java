package sgvic.entidades;

import java.util.Objects;

/**
 * Representa un cliente del estudio.
 * Mapea a la tabla 'cliente' (idCliente, razon_social, cuit, email, telefono, direccion).
 *
 */
public class Cliente {

    // === Atributos (encapsulados) ==
    private int idCliente;         // PK en BD
    private String razonSocial;    // NOT NULL
    private String cuit;           // UNIQUE en BD
    private String email;          // puede ser null
    private String telefono;       // puede ser null
    private String direccion;      // puede ser null

    // === Constructores ===
    public Cliente() {
        // vacío para frameworks/DAO
    }

    // Completo (sin id): útil para altas (el id lo pone la BD con AUTO_INCREMENT)
    public Cliente(String razonSocial, String cuit, String email, String telefono, String direccion) {
        this.razonSocial = razonSocial;
        this.cuit = cuit;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Completo (con id): útil para lecturas desde BD
    public Cliente(int idCliente, String razonSocial, String cuit, String email, String telefono, String direccion) {
        this.idCliente = idCliente;
        this.razonSocial = razonSocial;
        this.cuit = cuit;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // === Getters/Setters (encapsulamiento) ===
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    // === Validaciones livianas de dominio (se reforzarán en Service) ===
    public static boolean validarCuitBasico(String cuit) {
        return cuit != null && cuit.matches("\\d{11}");
    }

    public static boolean validarCuitConDV(String cuit) {
        return validarCuitBasico(cuit);
    }

    // === equals/hashCode/toString ===
    // Criterio: si el id ya existe (>0), usamos id; si no, usamos CUIT (caso objetos nuevos aún no persistidos).
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente other = (Cliente) o;

        if (this.idCliente > 0 && other.idCliente > 0) {
            return this.idCliente == other.idCliente;
        }
        return Objects.equals(this.cuit, other.cuit);
    }

    @Override
    public int hashCode() {
        if (idCliente > 0) return Integer.hashCode(idCliente);
        return Objects.hashCode(cuit);
    }

    @Override
    public String toString() {
    // Lo que se verá en los JComboBox, tablas, etc.
    return razonSocial;   // o getRazonSocial();
}

    
    

}
