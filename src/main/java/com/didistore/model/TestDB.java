
package com.didistore.model;

import com.didistore.config.Conexion;
import javax.jms.Connection;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class TestDB {
    public static void main(String[] args) {
        Connection cn = (Connection) Conexion.getConexion();
        
        if (cn != null) {
            System.err.println("¡Prueba superada! El Backend reconoce la base de datos. ");
        } else {
            System.err.println("Fallo la prueba: Revisa los parámetros. ");
        }
    }
}
