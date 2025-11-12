package sgvic.dao;

import sgvic.entidades.Cliente;
import sgvic.excepciones.DataAccessException;
import sgvic.config.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Cliente.
 * Implementa las operaciones CRUD contra la tabla 'cliente'.
 * Usa try-with-resources para cerrar automáticamente las conexiones.
 */
public class ClienteDAO implements Repositorio<Cliente> {

    private static final String INSERT =
            "INSERT INTO cliente (razon_social, cuit, email, telefono, direccion) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE cliente SET razon_social=?, cuit=?, email=?, telefono=?, direccion=? WHERE idCliente=?";

    private static final String DELETE =
            "DELETE FROM cliente WHERE idCliente=?";

    private static final String SELECT_ALL =
            "SELECT * FROM cliente";

    private static final String SELECT_BY_ID =
            "SELECT * FROM cliente WHERE idCliente=?";

    private static final String SELECT_BY_CUIT =
            "SELECT * FROM cliente WHERE cuit=?";

    @Override
    public void guardar(Cliente c) throws DataAccessException {
        try (Connection con = DB.getConnection()) {
            if (c.getIdCliente() > 0) {
                // UPDATE
                try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
                    ps.setString(1, c.getRazonSocial());
                    ps.setString(2, c.getCuit());
                    ps.setString(3, c.getEmail());
                    ps.setString(4, c.getTelefono());
                    ps.setString(5, c.getDireccion());
                    ps.setInt(6, c.getIdCliente());
                    ps.executeUpdate();
                }
            } else {
                // INSERT
                try (PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, c.getRazonSocial());
                    ps.setString(2, c.getCuit());
                    ps.setString(3, c.getEmail());
                    ps.setString(4, c.getTelefono());
                    ps.setString(5, c.getDireccion());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) c.setIdCliente(rs.getInt(1));
                    }
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // Esta excepción ocurre si se intenta insertar un CUIT duplicado
            throw new DataAccessException("El CUIT ya existe en la base de datos.", e);
        } catch (SQLException e) {
            throw new DataAccessException("Error al guardar cliente.", e);
        }
    }

    @Override
    public Cliente buscarPorId(int id) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCliente(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar cliente por ID.", e);
        }
        return null;
    }

    public Cliente buscarPorCuit(String cuit) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_CUIT)) {
            ps.setString(1, cuit);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCliente(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error al buscar cliente por CUIT.", e);
        }
        return null;
    }

    @Override
    public List<Cliente> listar() throws DataAccessException {
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapearCliente(rs));

        } catch (SQLException e) {
            throw new DataAccessException("Error al listar clientes.", e);
        }
        return lista;
    }

    @Override
    public void eliminar(int id) throws DataAccessException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error al eliminar cliente.", e);
        }
    }

    // --- Método auxiliar para mapear el ResultSet a un objeto Cliente ---
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("idCliente"),
                rs.getString("razon_social"),
                rs.getString("cuit"),
                rs.getString("email"),
                rs.getString("telefono"),
                rs.getString("direccion")
        );
    }
}

