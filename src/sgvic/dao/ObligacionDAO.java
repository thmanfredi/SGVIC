package sgvic.dao;

import sgvic.config.DB;
import sgvic.entidades.*;
import sgvic.entidades.TipoObligacion.Periodicidad;
import sgvic.excepciones.DataAccessException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ObligacionDAO implements Repositorio<Obligacion> {

    private static final String INSERT =
            "INSERT INTO obligacion (idCliente, idTipo, periodo, fechaVenc, monto, estado) VALUES (?,?,?,?,?,?)";

    private static final String UPDATE =
            "UPDATE obligacion SET idCliente=?, idTipo=?, periodo=?, fechaVenc=?, monto=?, estado=? WHERE idObligacion=?";

    private static final String DELETE =
            "DELETE FROM obligacion WHERE idObligacion=?";

    private static final String SELECT_BASE =
            "SELECT o.idObligacion, o.idCliente, o.idTipo, o.periodo, o.fechaVenc, o.monto, o.estado, " +
            "       c.razon_social, c.cuit, c.email, c.telefono, c.direccion, " +
            "       t.codigo, t.descripcion, t.periodicidad " +
            "FROM obligacion o " +
            "JOIN cliente c ON c.idCliente = o.idCliente " +
            "JOIN tipoobligacion t ON t.idTipo = o.idTipo ";

    private static final String SELECT_ALL = SELECT_BASE;
    private static final String SELECT_BY_ID = SELECT_BASE + " WHERE o.idObligacion=?";
    private static final String SELECT_BY_CLIENTE = SELECT_BASE + " WHERE o.idCliente=?";

    @Override
    public void guardar(Obligacion o) throws DataAccessException {
        try (Connection con = DB.getConnection()) {
            if (o.getIdObligacion() > 0) {
                try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
                    ps.setInt(1, o.getCliente().getIdCliente());
                    ps.setInt(2, o.getTipo().getIdTipo());
                    ps.setString(3, o.getPeriodo());
                    ps.setDate(4, Date.valueOf(o.getFechaVenc()));
                    ps.setBigDecimal(5, o.getMonto());
                    ps.setString(6, estadoToDb(o.getEstado())); // 'Pendiente','Vencida','Pagada'
                    ps.setInt(7, o.getIdObligacion());
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, o.getCliente().getIdCliente());
                    ps.setInt(2, o.getTipo().getIdTipo());
                    ps.setString(3, o.getPeriodo());
                    ps.setDate(4, Date.valueOf(o.getFechaVenc()));
                    ps.setBigDecimal(5, o.getMonto());
                    ps.setString(6, estadoToDb(o.getEstado()));
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) o.setIdObligacion(rs.getInt(1));
                    }
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // Puede ser por la UK (idCliente,idTipo,periodo) única en BD
            throw new DataAccessException("Ya existe una obligación para ese cliente, tipo y período.", e);
        } catch (SQLException e) {
            throw new DataAccessException("Error al guardar obligación.", e);
        }
    }

    @Override
    public Obligacion buscarPorId(int id) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearObligacion(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar obligación por ID.", e);
        }
    }

    @Override
    public List<Obligacion> listar() throws DataAccessException {
        List<Obligacion> lista = new ArrayList<>();
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapearObligacion(rs));
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar obligaciones.", e);
        }
    }

    public List<Obligacion> listarPorCliente(int idCliente) throws DataAccessException {
        List<Obligacion> lista = new ArrayList<>();
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_CLIENTE)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearObligacion(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar obligaciones por cliente.", e);
        }
    }

    @Override
    public void eliminar(int id) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar obligación.", e);
        }
    }

    // --- Helpers de mapeo ---
    private Obligacion mapearObligacion(ResultSet rs) throws SQLException {
        // Cliente (datos esenciales)
        Cliente c = new Cliente(
                rs.getInt("idCliente"),
                rs.getString("razon_social"),
                rs.getString("cuit"),
                rs.getString("email"),
                rs.getString("telefono"),
                rs.getString("direccion")
        );

        // Tipo
        TipoObligacion.Periodicidad per =
                Periodicidad.valueOf(rs.getString("periodicidad").toUpperCase());
        TipoObligacion t = new TipoObligacion(
                rs.getInt("idTipo"),
                rs.getString("codigo"),
                rs.getString("descripcion"),
                per
        );

        // Campos de obligación
        int idObl = rs.getInt("idObligacion");
        String periodo = rs.getString("periodo");           // "AAAA-MM"
        LocalDate fechaVenc = rs.getDate("fechaVenc").toLocalDate();
        BigDecimal monto = rs.getBigDecimal("monto");
        EstadoObligacion estado = dbToEstado(rs.getString("estado"));

        // Instanciar subclase según periodicidad (polimorfismo)
        Obligacion o;
        if (per == Periodicidad.ANUAL) {
            o = new ObligacionAnual(c, t, periodo, fechaVenc, monto, estado);
        } else {
            o = new ObligacionMensual(c, t, periodo, fechaVenc, monto, estado);
        }
        o.setIdObligacion(idObl);
        return o;
    }

    private String estadoToDb(EstadoObligacion e) {
        // En mi BD los valores son 'Pendiente','Vencida','Pagada' (con mayúscula inicial)
        switch (e) {
            case PENDIENTE: return "Pendiente";
            case VENCIDA:   return "Vencida";
            case PAGADA:    return "Pagada";
            default:        return "Pendiente";
        }
    }

    private EstadoObligacion dbToEstado(String s) {
        if ("Vencida".equalsIgnoreCase(s)) return EstadoObligacion.VENCIDA;
        if ("Pagada".equalsIgnoreCase(s))  return EstadoObligacion.PAGADA;
        return EstadoObligacion.PENDIENTE;
    }
}


