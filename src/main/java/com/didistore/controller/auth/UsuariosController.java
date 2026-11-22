
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.dao.interfaces.auth.IUsuariosDAO;
import com.didistore.model.auth.Usuarios;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // RF-13: derecho al olvido — anonimiza PII y conserva historial transaccional.
    public Map<String, Object> anonimizarUsuario(int idUsuario) {
        Map<String, Object> resultado = new HashMap<>();

        Usuarios usuario = usuariosDAO.consultarUsuariosPorId(idUsuario);
        if (usuario == null) {
            resultado.put("success", false);
            resultado.put("message", "Usuario no encontrado");
            return resultado;
        }

        if (usuariosDAO.tienePedidosActivos(idUsuario)) {
            resultado.put("success", false);
            resultado.put("message", "El usuario tiene pedidos activos. Resuélvelos antes de anonimizar.");
            return resultado;
        }

        boolean ok = usuariosDAO.anonimizarUsuario(idUsuario);
        resultado.put("success", ok);
        resultado.put("message", ok
                ? "Usuario anonimizado correctamente (PII eliminada, historial conservado)"
                : "No se pudo anonimizar el usuario");
        if (ok) {
            resultado.put("idUsuario", idUsuario);
            resultado.put("email", "usuario" + idUsuario + "@anonimizado.local");
        }
        return resultado;
    }

    public boolean tienePedidosActivos(int idUsuario) {
        return usuariosDAO.tienePedidosActivos(idUsuario);
    }

    public boolean registrarCliente(Usuarios usuario) {

        usuario.setperfilId(3);

        return usuariosDAO.registrarUsuario(usuario);
    }
}