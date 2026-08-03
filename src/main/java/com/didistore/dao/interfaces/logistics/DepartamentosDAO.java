
package com.didistore.dao.interfaces.logistics;

import com.didistore.config.Conexion;
import com.didistore.model.logistics.Departamentos;
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

// DAO (Data Access Object) clase para acceder a la tabla "departamentos" en la base de datos
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
