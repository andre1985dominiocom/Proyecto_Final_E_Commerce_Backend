
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
                Usuarios u = new Usuarios();
                u.setid_Usuario(rs.getInt("id_Usuario"));
                u.setemail(rs.getString("email"));
                u.setcontraseña(rs.getString("contraseña"));
                u.setnombre(rs.getString("nombre"));
                u.setapellido(rs.getString("apellido"));
                u.setdocumento(rs.getString("documento"));
                u.settipo_Documento(rs.getString("tipo_Documento"));
                u.setperfil_Id(rs.getInt("perfil_Id"));
                u.setestado(rs.getString("estado"));
                u.setemail_Verificado(rs.getInt("email_Verificado"));
                u.setfecha_Creacion(rs.getTimestamp("fecha_creacion"));
                u.setfecha_Actualizacion(rs.getTimestamp("fecha_Actualizacion"));
                u.setfecha_Ultimo_Login(rs.getTimestamp("fecha_Ultimo_Login"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
                    }
        
        return lista;
    }
}
