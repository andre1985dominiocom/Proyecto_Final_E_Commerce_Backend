package com.didistore.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Conexion {
    
    private static final String DATABASE = "db_e_commerce_didistore";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE;
    private static final String USER = "root";
    private static final String PASSWORD = "6719980";
    
    private static Connection conexion = null;
    
    public static Connection getConexion() {
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            if (conexion == null || conexion.isClosed()) {       
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a: " + DATABASE);
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Error: No se encontró el driver de MySQL (Revisa el pom.xml)");
        } catch (SQLException e) {
        System.err.println("Error de conexión: " + e.getMessage());
        }
        return conexion;    
    }
}