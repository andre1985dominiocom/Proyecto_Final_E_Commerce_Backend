
package com.didistore.dao.modulelogisticslocation;

import com.didistore.config.Conexion;
import com.didistore.model.modulelogisticslocation.Departamentos;
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
public class DepartamentosDAO {
    
    public List<Departamentos> listar() {
        List<Departamentos> lista = new ArrayList<>();
        String sql = "SELECT id_Departamento, departamento FROM departamentos";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                
            while (rs.next()) {
                Departamentos d = new Departamentos();
                d.setidDepartamento(rs.getInt("id_Departamento"));
                d.setdepartamento(rs.getString("departamento"));
                     
                lista.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar departamentos: " + e.getMessage());
                    }
            
        return lista;
    }
    
}
