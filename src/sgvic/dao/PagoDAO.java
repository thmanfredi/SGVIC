package sgvic.dao;

import sgvic.config.DB;
import sgvic.entidades.Obligacion;
import sgvic.entidades.Pago;
import sgvic.excepciones.DataAccessException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    private static final String INSERT =
        "INSERT INTO pago (idObligacion, fecha, medio, monto) VALUES (?,?,?,?)";

    private static final String SELECT_BY_OBL =
        "SELECT idPago, idObligacion, fecha, medio, monto FROM pago WHERE idObligacion=?";

    public void guardar(Pago p) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getObligacion().getIdObligacion());
            ps.setDate(2, Date.valueOf(p.getFecha()));
            ps.setString(3, p.getMedio());
            ps.setBigDecimal(4, p.getMonto());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setIdPago(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al registrar el pago.", e);
        }
    }

    public List<Pago> listarPorObligacion(Obligacion o) throws DataAccessException {
        List<Pago> lista = new ArrayList<>();
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_OBL)) {
            ps.setInt(1, o.getIdObligacion());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago p = new Pago();
                    p.setIdPago(rs.getInt("idPago"));
                    p.setObligacion(o);
                    p.setFecha(rs.getDate("fecha").toLocalDate());
                    p.setMedio(rs.getString("medio"));
                    p.setMonto(rs.getBigDecimal("monto"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al listar pagos de la obligación.", e);
        }
        return lista;
    }
}

