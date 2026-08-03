
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.dao.interfaces.auth.IUsuariosDAO;
import com.didistore.model.auth.Usuarios;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Controlador de usuarios, maneja la lógica de negocio y la comunicación con el DAO
public class UsuariosController {
    
    private final IUsuariosDAO usuariosDAO;

    public UsuariosController() {
        this.usuariosDAO = new UsuariosDAOImpl();
    }

    public List<Usuarios> listarUsuario() {
        return usuariosDAO.listarUsuarios();
    }

    public void insertarUsuario(Usuarios usuario) {
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        usuario.setfechaCreacion(ahora);
        usuario.setfechaActualizacion(ahora);
        usuario.setfechaUltimoLogin(ahora);

        usuariosDAO.insertarUsuarios(usuario);
    }

    public void actualizarUsuario(Usuarios usuario) {
        usuario.setfechaActualizacion(new Timestamp(System.currentTimeMillis()));
        usuariosDAO.actualizarUsuarios(usuario);
    }
    
    public Usuarios consultarUsuarioPorId(int idUsuario) {
        
        
        return usuariosDAO.consultarUsuariosPorId(idUsuario);
    }

    public void eliminarUsuario(int idUsuario) {
        usuariosDAO.eliminarUsuarios(idUsuario);
    }
    
    public boolean registrarCliente(Usuarios usuario) {
        
        usuario.setperfilId(3);
        
        return usuariosDAO.registrarUsuario(usuario);
    }
}