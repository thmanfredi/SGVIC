package sgvic.dao;

import java.util.List;
import sgvic.excepciones.DataAccessException;

/**
 * Interfaz genérica de acceso a datos.
 * Define operaciones CRUD básicas (Create, Read, Update, Delete).
 * Implementada por cada DAO (ClienteDAO, ObligacionDAO, etc.).
 */
public interface Repositorio<T> {

    void guardar(T entidad) throws DataAccessException;      // CREATE o UPDATE
    T buscarPorId(int id) throws DataAccessException;        // READ (por ID)
    List<T> listar() throws DataAccessException;             // READ (todos)
    void eliminar(int id) throws DataAccessException;        // DELETE
}
