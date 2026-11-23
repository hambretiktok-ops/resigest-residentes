package co.edu.sena.resigest.config;

import co.edu.sena.resigest.exception.DataAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Administra la conexion JDBC con la base de datos MySQL de ResiGest.
 * Lee la URL, el usuario y la clave desde el archivo db.properties.
 *
 * @author Edwar Stiven Diaz Torres
 */
public final class DatabaseConnection {

    private static final String PROPERTIES_FILE = "db.properties";
    private static final Properties DB_PROPERTIES = loadProperties();

    /** Clase utilitaria: no se debe instanciar. */
    private DatabaseConnection() {
    }

    /**
     * Abre una nueva conexion a la base de datos.
     * Quien la usa debe cerrarla (se recomienda try-with-resources).
     *
     * @return conexion activa con MySQL
     * @throws DataAccessException si no se puede conectar
     */
    public static Connection getConnection() {
        String url = DB_PROPERTIES.getProperty("db.url");
        String user = DB_PROPERTIES.getProperty("db.user");
        String password = DB_PROPERTIES.getProperty("db.password");
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new DataAccessException("No fue posible conectar con la base de datos: " + ex.getMessage(), ex);
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new DataAccessException("No se encontro el archivo " + PROPERTIES_FILE, null);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new DataAccessException("Error leyendo " + PROPERTIES_FILE, ex);
        }
        return properties;
    }
}
