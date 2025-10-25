package sgvic.servicios;

import sgvic.dao.ClienteDAO;
import sgvic.entidades.Cliente;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;

import java.util.List;

/**
 * Reglas de negocio para Cliente.
 * - Valida formato y campos requeridos.
 * - Evita CUIT duplicado con chequeo previo y/o captura de constraint.
 * - Delegación al DAO para persistencia.
 */
public class ClienteService {

    private final ClienteDAO dao = new ClienteDAO();

    // === Validaciones internas (texto claro para RNF06) ===
    private void validarParaAlta(Cliente c) throws DomainException {
        if (c == null) throw new DomainException("Cliente inválido (null).");
        if (c.getRazonSocial() == null || c.getRazonSocial().isBlank())
            throw new DomainException("La razón social es obligatoria.");
        if (c.getCuit() == null || !Cliente.validarCuitBasico(c.getCuit()))
            throw new DomainException("El CUIT debe tener 11 dígitos numéricos.");

        // Chequeo amable de duplicado (además de la constraint UNIQUE en BD)
        // Si ya existe, avisamos antes de ir al INSERT para dar mejor mensaje.
        // La BD igualmente protege con UNIQUE (cuit).
        // (Si otro proceso insertó en el medio, capturamos el error en DAO.)
        try {
            Cliente existente = dao.buscarPorCuit(c.getCuit());
            if (existente != null) {
                throw new DomainException("El CUIT ya está registrado.");
            }
        } catch (DataAccessException e) {
            // Propagamos como Domain si queremos mantener el mensaje funcional
            throw new DomainException("No se pudo verificar duplicados de CUIT.", e);
        }
    }

    private void validarParaActualizar(Cliente c) throws DomainException {
        if (c == null || c.getIdCliente() <= 0)
            throw new DomainException("Cliente inválido para actualizar (ID requerido).");
        if (c.getRazonSocial() == null || c.getRazonSocial().isBlank())
            throw new DomainException("La razón social es obligatoria.");
        if (c.getCuit() == null || !Cliente.validarCuitBasico(c.getCuit()))
            throw new DomainException("El CUIT debe tener 11 dígitos numéricos.");
    }

    // === Operaciones públicas ===

    /** Alta de cliente con validaciones de dominio. */
    public void alta(Cliente c) throws DomainException, DataAccessException {
        validarParaAlta(c);
        dao.guardar(c);
    }

    /** Actualización de datos del cliente (incluye CUIT si fuera necesario). */
    public void actualizar(Cliente c) throws DomainException, DataAccessException {
        validarParaActualizar(c);
        // Caso borde: si cambia el CUIT, chequeamos que no choque con otro registro
        Cliente existente = dao.buscarPorCuit(c.getCuit());
        if (existente != null && existente.getIdCliente() != c.getIdCliente()) {
            throw new DomainException("No se puede actualizar: el nuevo CUIT ya pertenece a otro cliente.");
        }
        dao.guardar(c);
    }

    /** Borrado por ID. */
    public void eliminar(int idCliente) throws DomainException, DataAccessException {
        if (idCliente <= 0) throw new DomainException("ID de cliente inválido.");
        dao.eliminar(idCliente);
    }

    /** Búsqueda por CUIT (útil para el menú). */
    public Cliente buscarPorCuit(String cuit) throws DomainException, DataAccessException {
        if (cuit == null || !Cliente.validarCuitBasico(cuit))
            throw new DomainException("CUIT inválido para búsqueda.");
        return dao.buscarPorCuit(cuit);
    }

    /** Listado completo. */
    public List<Cliente> listar() throws DataAccessException {
        return dao.listar();
    }
}

