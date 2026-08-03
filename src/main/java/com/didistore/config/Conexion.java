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
    public static Connection getConexion() throws SQLException {

        String dbName = props.getProperty("db.name");
        String dbUser = props.getProperty("db.user");
        String dbPassword = props.getProperty("db.password");
        String dbHost = props.getProperty("db.host");
        String dbPort = props.getProperty("db.port");

        String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

        return DriverManager.getConnection(url, dbUser, dbPassword);
    }
}