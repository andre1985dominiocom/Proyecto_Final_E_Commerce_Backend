
package com.didistore.dao.interfaces.logistics;

import com.didistore.config.Conexion;
import com.didistore.model.logistics.Ciudades;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// DAO (Data Access Object) clase para acceder a la tabla "ciudades" en la base de datos
public class CiudadesDAO {
    
    public List<Ciudades> listar() {
        List<Ciudades> lista = new ArrayList<>();
        String sql = "SELECT id_Ciudad, nombre_Ciudad, departamento_Id, codigo_Postal FROM ciudades";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                
            while (rs.next()) {
                Ciudades city = new Ciudades();
                city.setidCiudad(rs.getInt("id_Ciudad"));
                city.setnombreCiudad(rs.getString("nombre_Ciudad"));
                city.setdepartamentoId(rs.getInt("departamento_Id"));
                city.setcodigoPostal(rs.getString("codigo_Postal"));
                lista.add(city);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ciudades: " + e.getMessage());
                    }
            
        return lista;
    }
}