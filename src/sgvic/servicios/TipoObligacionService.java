package sgvic.servicios;

import sgvic.dao.TipoObligacionDAO;
import sgvic.entidades.TipoObligacion;
import sgvic.excepciones.DataAccessException;

import java.util.List;

public class TipoObligacionService {

    private final TipoObligacionDAO dao = new TipoObligacionDAO();

    public List<TipoObligacion> listarTodos() throws DataAccessException {
        return dao.listar();
    }
}

