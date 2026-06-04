
package com.didistore.dao.interfaces.auth;

import com.didistore.model.auth.Usuarios;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para la gestión de usuarios en la base de datos.
public interface IUsuariosDAO {
    
    void insertarUsuarios(Usuarios usuario);
    
    List<Usuarios> listarUsuarios();
    
    Usuarios consultarUsuariosPorId(int idUsuario);
    
    Usuarios consultarUsuariosPorEmail(String email);
    
    void actualizarUsuarios(Usuarios usuario);
    
    void actualizarContrasena(int idUsuario, String nuevaContrasena);
    
    void eliminarUsuarios(int idUsuario);
}