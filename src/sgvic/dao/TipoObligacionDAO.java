package sgvic.dao;

import sgvic.config.DB;
import sgvic.entidades.TipoObligacion;
import sgvic.entidades.TipoObligacion.Periodicidad;
import sgvic.excepciones.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoObligacionDAO {

    private static final String SELECT_BY_ID =
            "SELECT idTipo, codigo, descripcion, periodicidad FROM tipoobligacion WHERE idTipo=?";

    public TipoObligacion buscarPorId(int idTipo) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, idTipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new TipoObligacion(
                        rs.getInt("idTipo"),
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        Periodicidad.valueOf(rs.getString("periodicidad").toUpperCase()) // Mensual/Anual/Otra → enum
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar TipoObligacion por ID.", e);
        }
    }
    public List<TipoObligacion> listar() throws DataAccessException {
    List<TipoObligacion> lista = new ArrayList<>();
    String sql = "SELECT idTipo, codigo, descripcion, periodicidad FROM tipoobligacion";
    try (Connection con = DB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            lista.add(new TipoObligacion(
                    rs.getInt("idTipo"),
                    rs.getString("codigo"),
                    rs.getString("descripcion"),
                    Periodicidad.valueOf(rs.getString("periodicidad").toUpperCase())
            ));
        }
    } catch (SQLException e) {
        throw new DataAccessException("Error al listar tipos de obligación.", e);
    }
    return lista;
}

}
