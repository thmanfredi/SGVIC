package sgvic.servicios;

import sgvic.dao.ObligacionDAO;
import sgvic.dao.PagoDAO;
import sgvic.entidades.EstadoObligacion;
import sgvic.entidades.Obligacion;
import sgvic.entidades.Pago;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;
import sgvic.excepciones.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de pagos.
 * Se encarga de:
 *  - Registrar un pago para una obligación.
 *  - Actualizar el estado de la obligación a PAGADA.
 *  - Listar pagos de una obligación.
 */
public class PagoService {

    private final PagoDAO pagoDAO;
    private final ObligacionDAO obligacionDAO;

    public PagoService() {
        this.pagoDAO = new PagoDAO();
        this.obligacionDAO = new ObligacionDAO();
    }

    /**
     * Registra un pago para la obligación indicada y cambia su estado a PAGADA.
     */
    public void registrarPago(int idObligacion,
                              LocalDate fecha,
                              String medio,
                              BigDecimal monto)
            throws DataAccessException, DomainException, NotFoundException {

        // Buscar la obligación
        Obligacion obl = obligacionDAO.buscarPorId(idObligacion);
        if (obl == null) {
            throw new NotFoundException("No existe obligación con ID " + idObligacion);
        }

        if (obl.getEstado() == EstadoObligacion.PAGADA) {
            throw new DomainException("La obligación ya está pagada.");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("El monto del pago debe ser mayor a cero.");
        }

        // Crear pago
        Pago p = new Pago();
        p.setObligacion(obl);
        p.setFecha(fecha != null ? fecha : LocalDate.now());
        p.setMedio(medio != null && !medio.isBlank() ? medio.trim() : "Sin especificar");
        p.setMonto(monto);

        // Guardar pago en la BD
        pagoDAO.guardar(p);

        // Actualizar estado de obligación
        obl.setEstado(EstadoObligacion.PAGADA);
        obligacionDAO.guardar(obl);
    }

    /**
     * Lista los pagos asociados a una obligación.
     */
    public List<Pago> listarPorObligacion(int idObligacion)
            throws DataAccessException, NotFoundException {

        Obligacion obl = obligacionDAO.buscarPorId(idObligacion);
        if (obl == null) {
            throw new NotFoundException("No existe obligación con ID " + idObligacion);
        }
        return pagoDAO.listarPorObligacion(obl);
    }
}
