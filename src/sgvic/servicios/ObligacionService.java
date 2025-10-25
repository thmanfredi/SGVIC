package sgvic.servicios;

import sgvic.dao.ObligacionDAO;
import sgvic.entidades.*;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;
import sgvic.excepciones.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ObligacionService {

    private final ObligacionDAO dao = new ObligacionDAO();

    // --- Validaciones básicas ---
    private void validarObligacion(Obligacion o) throws DomainException {
        if (o == null) throw new DomainException("Obligación inválida (null).");
        if (o.getCliente() == null || o.getCliente().getIdCliente() <= 0)
            throw new DomainException("Cliente inválido para la obligación.");
        if (o.getTipo() == null || o.getTipo().getIdTipo() <= 0)
            throw new DomainException("Tipo de obligación inválido.");
        if (o.getPeriodo() == null || !o.getPeriodo().matches("\\d{4}-\\d{2}"))
            throw new DomainException("Período inválido. Formato esperado AAAA-MM.");
        if (o.getFechaVenc() == null)
            throw new DomainException("La fecha de vencimiento es obligatoria.");
        if (o.getMonto() == null || o.getMonto().compareTo(BigDecimal.ZERO) <= 0)
            throw new DomainException("El monto debe ser mayor a 0.");
        if (o.getEstado() == null)
            throw new DomainException("El estado es obligatorio.");
    }

    // --- API pública ---
    public void alta(Obligacion o) throws DomainException, DataAccessException {
        validarObligacion(o);
        // Dejar que la BD haga cumplir la UK (idCliente,idTipo,periodo) y capturar mensaje amable en DAO.
        dao.guardar(o);
    }

    public void actualizar(Obligacion o) throws DomainException, DataAccessException {
        if (o.getIdObligacion() <= 0) throw new DomainException("ID inválido para actualizar.");
        validarObligacion(o);
        dao.guardar(o);
    }

    public Obligacion buscarPorId(int id) throws DataAccessException, NotFoundException {
        Obligacion o = dao.buscarPorId(id);
        if (o == null) throw new NotFoundException("Obligación no encontrada.");
        return o;
    }

    public List<Obligacion> listar() throws DataAccessException {
        return dao.listar();
    }

    public List<Obligacion> listarPorCliente(int idCliente) throws DataAccessException {
        return dao.listarPorCliente(idCliente);
    }

    public void eliminar(int idObligacion) throws DataAccessException, DomainException {
        if (idObligacion <= 0) throw new DomainException("ID inválido para eliminar.");
        dao.eliminar(idObligacion);
    }

    // --- Ordenación por fecha de vencimiento (ascendente) ---
    public void ordenarPorVencimiento(List<Obligacion> lista) {
        if (lista == null) return;
        lista.sort(Comparator.comparing(Obligacion::getFechaVenc));
    }

    // --- Búsqueda binaria por PERÍODO (sobre lista ORDENADA por PERÍODO) ---
    public int ordenarPorPeriodoYBuscarBinario(List<Obligacion> lista, String periodo) {
        if (lista == null || periodo == null) return -1;
        // 1) Ordenamos por período (String AAAA-MM funciona lexicográficamente)
        lista.sort(Comparator.comparing(Obligacion::getPeriodo));
        // 2) Búsqueda binaria clásica
        int lo = 0, hi = lista.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            String p = lista.get(mid).getPeriodo();
            int cmp = p.compareTo(periodo);
            if (cmp == 0) return mid;
            if (cmp < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    // --- Marcar pagada (se completará con PagoService luego) ---
    public void marcarPagada(Obligacion o) throws DomainException, DataAccessException {
        if (o == null || o.getIdObligacion() <= 0)
            throw new DomainException("Obligación inválida para marcar como pagada.");
        o.marcarPagada();
        dao.guardar(o);
    }
}

