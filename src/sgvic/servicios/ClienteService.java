package sgvic.servicios;

import sgvic.dao.ClienteDAO;
import sgvic.entidades.Cliente;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;

import java.util.List;

/**
 * Servicio de clientes.
 * Capa intermedia entre la UI y el DAO.
 * Acá aplico validaciones de negocio y delego al ClienteDAO.
 */
public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    /**
     * Devuelve todos los clientes de la BD.
     */
    public List<Cliente> listar() throws DataAccessException {
        return clienteDAO.listar();
    }

    /**
     * Crea y guarda un nuevo cliente validando datos básicos.
     * Este método lo llama la interfaz Swing.
     */
    public void guardarNuevoCliente(String razonSocial,
                                    String cuit,
                                    String email,
                                    String telefono,
                                    String direccion)
            throws DomainException, DataAccessException {

        // Validaciones simples de dominio
        if (razonSocial == null || razonSocial.isBlank()) {
            throw new DomainException("La razón social es obligatoria.");
        }
        if (cuit == null || cuit.isBlank()) {
            throw new DomainException("El CUIT es obligatorio.");
        }

        // Creo el objeto de dominio
        Cliente c = new Cliente();
        c.setRazonSocial(razonSocial.trim());
        c.setCuit(cuit.trim());
        c.setEmail(email != null ? email.trim() : null);
        c.setTelefono(telefono != null ? telefono.trim() : null);
        c.setDireccion(direccion != null ? direccion.trim() : null);

        // El DAO se encarga de persistir en la BD (INSERT/UPDATE)
        clienteDAO.guardar(c);
    }
}

