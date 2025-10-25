package sgvic.ui;

import java.sql.Connection;
import java.sql.SQLException;
import sgvic.config.DB;

public class ProbarConexion {
    public static void main(String[] args) {
        try (Connection con = DB.getConnection()) {
            System.out.println("✅ Conexión exitosa a la base de datos SGVIC!");
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
        }
    }
}
