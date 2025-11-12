package sgvic.dao;

import sgvic.config.DB;
import sgvic.entidades.Alerta;
import sgvic.excepciones.DataAccessException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos para 'alerta' (idAlerta, idObligacion, fecha, leida).
 */
public class AlertaDAO {

    private static final String INSERT =
            "INSERT INTO alerta (idObligacion, fecha, leida) VALUES (?,?,?)";

    private static final String SELECT_PENDIENTES =
            "SELECT idAlerta, idObligacion, fecha, leida FROM alerta WHERE leida=0 ORDER BY fecha ASC";

    private static final String UPDATE_MARCAR_LEIDA =
            "UPDATE alerta SET leida=1 WHERE idAlerta=?";

    private static final String EXISTS_POR_OBL_FECHA =
            "SELECT 1 FROM alerta WHERE idObligacion=? AND fecha=? LIMIT 1";

    public void guardar(Alerta a) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getObligacion().getIdObligacion());
            ps.setDate(2, Date.valueOf(a.getFecha()));
            ps.setBoolean(3, a.isLeida());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setIdAlerta(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al guardar alerta.", e);
        }
    }

    public List<Alerta> listarPendientes() throws DataAccessException {
        List<Alerta> lista = new ArrayList<>();
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_PENDIENTES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Alerta a = new Alerta();
                a.setIdAlerta(rs.getInt("idAlerta"));
                a.setObligacion(null);
                a.setFecha(rs.getDate("fecha").toLocalDate());
                a.setLeida(rs.getBoolean("leida"));
                lista.add(a);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar alertas pendientes.", e);
        }
        return lista;
    }

    public void marcarLeida(int idAlerta) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_MARCAR_LEIDA)) {
            ps.setInt(1, idAlerta);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al marcar alerta como leída.", e);
        }
    }

    public boolean existePara(int idObligacion, LocalDate fecha) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(EXISTS_POR_OBL_FECHA)) {
            ps.setInt(1, idObligacion);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al verificar existencia de alerta.", e);
        }
    }
}
