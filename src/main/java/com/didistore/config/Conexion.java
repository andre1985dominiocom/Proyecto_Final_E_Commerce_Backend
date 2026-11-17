package com.didistore.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase para manejar la conexión a la base de datos MySQL
public class Conexion {
    
    // Propiedades para almacenar la configuración de la base de datos
    private static final Properties props = new Properties();

    // Bloque estático para cargar el driver de MySQL y las propiedades de la base de datos
        static {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                try (InputStream input =
                        Conexion.class.getClassLoader().getResourceAsStream("database.properties")) {

                    if (input == null) {
                        throw new RuntimeException("No se encontró database.properties");
                    }
                    props.load(input);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Driver MySQL no encontrado", e);
            } catch (IOException e) {
                throw new RuntimeException("Error cargando database.properties",e);
            }
        }
    
    // Método para obtener la conexión a la base de datos
    // Prioridad: variables de entorno (DB_*) > database.properties
    public static Connection getConexion() throws SQLException {

        String dbName = envOrProp("DB_NAME", "db.name");
        String dbUser = envOrProp("DB_USER", "db.user");
        String dbPassword = envOrProp("DB_PASSWORD", "db.password");
        String dbHost = envOrProp("DB_HOST", "db.host");
        String dbPort = envOrProp("DB_PORT", "db.port");

        String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

        return DriverManager.getConnection(url, dbUser, dbPassword);
    }

    private static String envOrProp(String envKey, String propKey) {
        String value = System.getenv(envKey);
        if (value == null || value.isBlank()) {
            value = props.getProperty(propKey);
        }
        return value;
    }
}