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
    
<<<<<<< HEAD
    private static final String DATABASE = System.getenv("DB_NAME") != null
            ? System.getenv("DB_NAME") : "db_e_commerce_didistore";
    private static final String URL = "jdbc:mysql://"
            + (System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost")
            + ":3306/" + DATABASE;
    private static final String USER = System.getenv("DB_USER") != null
            ? System.getenv("DB_USER") : "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null
            ? System.getenv("DB_PASSWORD") : "";
    
    private static Connection conexion = null;
    
    public static Connection getConexion() {
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a: " + DATABASE);
=======
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
>>>>>>> origin/main
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