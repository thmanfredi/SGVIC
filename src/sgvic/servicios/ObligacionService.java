package sgvic.servicios;

import sgvic.dao.ObligacionDAO;
import sgvic.entidades.Cliente;
import sgvic.entidades.EstadoObligacion;
import sgvic.entidades.Obligacion;
import sgvic.entidades.ObligacionMensual;
import sgvic.entidades.TipoObligacion;
import sgvic.excepciones.DataAccessException;
import sgvic.excepciones.DomainException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Servicio de obligaciones fiscales.
 * Acá se concentra la lógica de negocio:
 *  - Listar obligaciones desde la BD.
 *  - Ordenarlas por fecha de vencimiento.
 *  - Buscar una obligación por período usando búsqueda binaria.
 *  - Crear nuevas obligaciones y guardarlas en la BD.
 */
public class ObligacionService {

    private final ObligacionDAO obligacionDAO;

    public ObligacionService() {
        this.obligacionDAO = new ObligacionDAO();
    }

    /**
     * Devuelve todas las obligaciones de la BD.
     */
    public List<Obligacion> listar() throws DataAccessException {
        return obligacionDAO.listar();
    }

    /**
     * Devuelve una NUEVA lista ordenada por fecha de vencimiento (ascendente).
     * No modifica la lista original.
     */
    public List<Obligacion> ordenarPorVencimiento(List<Obligacion> origen) {
        List<Obligacion> copia = new ArrayList<>(origen);
        copia.sort(Comparator.comparing(Obligacion::getFechaVenc));
        return copia;
    }

    /**
     * Búsqueda binaria por período (ej: "2025-03").
     * Trabaja sobre una copia de la lista, ordenada por período.
     * Si no encuentra el período, devuelve null.
     */
    public Obligacion buscarPorPeriodo(List<Obligacion> origen, String periodoBuscado) {
        if (origen == null || origen.isEmpty()) return null;
        if (periodoBuscado == null || periodoBuscado.isBlank()) return null;

        String clave = periodoBuscado.trim();

        // Hago una copia y la ordeno por período (String "AAAA-MM")
        List<Obligacion> copia = new ArrayList<>(origen);
        copia.sort(Comparator.comparing(Obligacion::getPeriodo));

        int low = 0;
        int high = copia.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Obligacion current = copia.get(mid);
            int cmp = current.getPeriodo().compareTo(clave);

            if (cmp == 0) {
                return current; // encontrada
            } else if (cmp < 0) {
                low = mid + 1;  // buscar en mitad superior
            } else {
                high = mid - 1; // buscar en mitad inferior
            }
        }
        return null; // no encontrada
    }

    /**
     * Crea una nueva obligación y la persiste en la BD.
     * Valida los datos de entrada y, si son correctos,
     * instancia una ObligacionMensual (o la clase concreta que uses)
     * en estado PENDIENTE y la guarda con el DAO.
     */
    public void crearObligacion(Cliente cliente,
                                TipoObligacion tipo,
                                String periodo,
                                LocalDate fechaVenc,
                                BigDecimal monto) throws DomainException, DataAccessException {

        if (cliente == null) {
            throw new DomainException("Debe seleccionar un cliente.");
        }
        if (tipo == null) {
            throw new DomainException("Debe seleccionar un tipo de obligación.");
        }
        if (periodo == null || !periodo.matches("\\d{4}-\\d{2}")) {
            throw new DomainException("El período debe tener formato AAAA-MM.");
        }
        if (fechaVenc == null) {
            throw new DomainException("Debe ingresar una fecha de vencimiento válida.");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("El monto debe ser mayor a cero.");
        }

        // Uso ObligacionMensual como implementación concreta.
        // Si en algún caso querés anual, podés decidirlo según la periodicidad del tipo.
        Obligacion nueva = new ObligacionMensual(
                cliente,
                tipo,
                periodo,
                fechaVenc,
                monto,
                EstadoObligacion.PENDIENTE
        );

        obligacionDAO.guardar(nueva);
    }
}
