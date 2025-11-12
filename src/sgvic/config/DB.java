package sgvic.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB {

    private static final Properties props = new Properties();

    static {
        try {
            // Cargar archivo db.properties desde el classpath (funciona dentro del .jar)
            InputStream in = DB.class.getClassLoader().getResourceAsStream("sgvic/config/db.properties");
            if (in == null) {
                throw new RuntimeException("No se encontró el archivo db.properties en el classpath");
            }
            props.load(in);

            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error cargando configuración de BD: " + e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("url"),
                props.getProperty("user"),
                props.getProperty("password")
        );
    }
}

