
package com.didistore.dao;

import com.didistore.config.Conexion;
import com.didistore.model.Usuarios;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class UsuariosDAO {
    public List<Usuarios> listar() {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT id_Usuario, email, contraseña, nombre, apellido, documento, tipo_Documento, perfil_Id, estado, email_Verificado, fecha_Creacion, fecha_Actualizacion, Fecha_Ultimo_Login FROM usuarios";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
                 
             while (rs.next()) {
                 Usuarios p = new Usuarios();
                 p.setid_Usuario(rs.getInt("id_Usuario"));
                 p.setemail(rs.getString("email"));
                 p.setcontraseña(rs.getString("contraseña"));
                 p.setnombre(rs.getString("nombre"));
                 p.setapellido(rs.getString("apellido"));
                 p.setdocumento(rs.getString("documento"));
                 p.settipo_Documento(rs.getString("tipo_Documento"));
                 p.setperfil_Id(rs.getInt("perfil_Id"));
                 p.setestado(rs.getString("estado"));
                 p.setemail_Verificado(rs.getInt("email_Verificado"));
                 p.setfecha_Creacion(rs.getTimestamp("fecha_creacion"));
                 p.setfecha_Actualizacion(rs.getTimestamp("fecha_Actualizacion"));
                 p.setfecha_Ultimo_Login(rs.getTimestamp("fecha_Ultimo_Login"));
                 lista.add(p);
             }
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
                     }
             
        return lista;
    }
}
