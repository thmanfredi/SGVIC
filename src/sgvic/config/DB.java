package sgvic.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase DB
 * Encargada de manejar la conexión a la base de datos MySQL.
 * Lee los datos desde el archivo db.properties ubicado en sgvic/config.
 */
public class DB {

    // Método que devuelve una conexión lista para usar
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream("src/sgvic/config/db.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("⚠️ No se pudo leer el archivo db.properties: " + e.getMessage());
        }

        String url = props.getProperty("url");
        String user = props.getProperty("user");
        String password = props.getProperty("password");

        // Intenta conectar
        return DriverManager.getConnection(url, user, password);
    }
}
