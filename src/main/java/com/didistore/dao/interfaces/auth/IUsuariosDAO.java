
package com.didistore.dao.interfaces.auth;

import com.didistore.model.auth.Usuarios;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface IUsuariosDAO {
    
    void insertarUsuarios(Usuarios usuario);
    
    List<Usuarios> listarUsuarios();
    
    Usuarios consultarUsuariosPorId(int idUsuario);
    
    Usuarios consultarUsuariosPorEmail(String email);
    
    void actualizarUsuarios(Usuarios usuario);
    
    void eliminarUsuarios(int idUsuario);
}