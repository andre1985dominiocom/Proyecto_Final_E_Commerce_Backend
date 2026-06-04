package com.didistore.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Conexion {

    private static final String DEFAULT_DATABASE = "db_e_commerce_didistore";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "6719980";

    private static final String DATABASE = leerVariable("DIDISTORE_DB_NAME", DEFAULT_DATABASE);
    private static final String HOST = leerVariable("DIDISTORE_DB_HOST", DEFAULT_HOST);
    private static final String PORT = leerVariable("DIDISTORE_DB_PORT", DEFAULT_PORT);
    private static final String USER = leerVariable("DIDISTORE_DB_USER", DEFAULT_USER);
    private static final String PASSWORD = leerVariable("DIDISTORE_DB_PASSWORD", DEFAULT_PASSWORD);
    private static final String URL = leerVariable(
            "DIDISTORE_DB_URL",
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );

    public static Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a: " + DATABASE);
            return conexion;
        } catch (ClassNotFoundException ex) {
            System.err.println("Error: No se encontró el driver de MySQL (Revisa el pom.xml)");
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
        return null;
    }

    private static String leerVariable(String nombre, String valorPorDefecto) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            return valorPorDefecto;
        }
        return valor.trim();
    }
}
