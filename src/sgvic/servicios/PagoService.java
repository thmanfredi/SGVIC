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

public class PagoService {

    private final PagoDAO pagoDAO = new PagoDAO();
    private final ObligacionDAO obligacionDAO = new ObligacionDAO();

    /**
     * Registra un pago y marca la obligación como PAGADA.
     * Flujo:
     * 1) Buscar obligación
     * 2) Validar (no nula, no pagada, monto > 0)
     * 3) Guardar pago
     * 4) Cambiar estado a PAGADA y persistir obligación
     */
    public void registrarPago(int idObligacion,
                              LocalDate fecha,
                              String medio,
                              BigDecimal monto)
            throws DomainException, DataAccessException, NotFoundException {

        // 1) Buscar obligación
        Obligacion o = obligacionDAO.buscarPorId(idObligacion);
        if (o == null) throw new NotFoundException("Obligación no encontrada.");

        // 2) Validaciones de dominio
        if (o.getEstado() == EstadoObligacion.PAGADA)
            throw new DomainException("La obligación ya está pagada.");
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new DomainException("El monto del pago debe ser > 0.");
        if (fecha == null) fecha = LocalDate.now();

        // 3) Guardar pago
        Pago p = new Pago(o, fecha, medio, monto);
        pagoDAO.guardar(p);

        // 4) Cambiar estado de la obligación y persistir
        o.marcarPagada();
        obligacionDAO.guardar(o);
    }
}
