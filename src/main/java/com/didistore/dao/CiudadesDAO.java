
package com.didistore.dao;

import com.didistore.config.Conexion;
import com.didistore.model.Ciudades;
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
public class CiudadesDAO {
    
    public List<Ciudades> listar() {
        List<Ciudades> lista = new ArrayList<>();
        String sql = "SELECT id_Ciudades, nombre_Ciudad, departamento_Id, codigo_Postal FROM ciudades";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                
            while (rs.next()) {
                Ciudades city = new Ciudades();
                city.setid_Ciudad(rs.getInt("id_Ciudad"));
                city.setnombre_Ciudad(rs.getString("nombre_Ciudad"));
                city.setdepartamento_Id(rs.getInt("departamento_Id"));
                city.setcodigo_Postal(rs.getString("codigo_Postal"));
                     
                lista.add(city);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ciudades: " + e.getMessage());
                    }
            
        return lista;
    }
}