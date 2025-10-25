package sgvic.servicios;

import sgvic.dao.AlertaDAO;
import sgvic.dao.ObligacionDAO;
import sgvic.entidades.Alerta;
import sgvic.entidades.EstadoObligacion;
import sgvic.entidades.Obligacion;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Lógica de alertas:
 * - Generar alertas para obligaciones vencidas o que vencen en X días.
 * - Mantener una cola FIFO para procesar "pendientes".
 */
public class AlertaService {

    private final AlertaDAO alertaDAO = new AlertaDAO();
    private final ObligacionDAO obligacionDAO = new ObligacionDAO();

    /**
     * Genera alertas para:
     *  a) obligaciones VENCIDAS (fechaVenc < hoy, estado != PAGADA)
     *  b) obligaciones que vencen en <= diasAviso (inclusive), estado != PAGADA
     * Evita duplicados verificando (idObligacion, fecha=hoy) en la tabla 'alerta'.
     * Devuelve una Queue con las nuevas alertas generadas (FIFO).
     */
    public Deque<Alerta> generarPendientes(LocalDate hoy, int diasAviso) throws DataAccessException {
        if (hoy == null) hoy = LocalDate.now();
        if (diasAviso < 0) diasAviso = 0;

        List<Obligacion> todas = obligacionDAO.listar();
        Deque<Alerta> cola = new ArrayDeque<>();

        for (Obligacion o : todas) {
            if (o.getEstado() == EstadoObligacion.PAGADA) continue;
            if (o.getFechaVenc() == null) continue;

            boolean vencida = o.getFechaVenc().isBefore(hoy);
            boolean porVencer = !vencida && !o.getFechaVenc().isAfter(hoy.plusDays(diasAviso));

            if (vencida || porVencer) {
                // evitamos duplicar alerta “del día” (fecha = hoy)
                if (!alertaDAO.existePara(o.getIdObligacion(), hoy)) {
                    Alerta a = new Alerta(o, hoy, false);
                    alertaDAO.guardar(a);
                    cola.add(a);
                }
            }
        }
        return cola;
    }

    /** Devuelve una Queue con todas las alertas no leídas (desde BD) en orden FIFO. */
    public Deque<Alerta> listarPendientes() throws DataAccessException {
        Deque<Alerta> cola = new ArrayDeque<>();
        alertaDAO.listarPendientes().forEach(cola::add);
        return cola;
    }

    /** Marca como leída una alerta por su ID. */
    public void marcarLeida(int idAlerta) throws DomainException, DataAccessException {
        if (idAlerta <= 0) throw new DomainException("ID de alerta inválido.");
        alertaDAO.marcarLeida(idAlerta);
    }
}

