
package com.didistore.controller.logistics;

import com.didistore.dao.impl.logistics.DireccionesDAOImpl;
import com.didistore.dao.interfaces.logistics.IDireccionesDAO;
import com.didistore.model.logistics.Direcciones;
import com.didistore.model.logistics.enums.EstadoDirecciones;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Controlador para manejar las operaciones relacionadas con las direcciones de los usuarios.
public class DireccionesController {
    
    private final IDireccionesDAO direccionesDAO;
    
    public DireccionesController() {
        this.direccionesDAO = new DireccionesDAOImpl();
    }
    
    public List<Direcciones> obtenerDireccionesUsuario(int usuarioId) {
        return direccionesDAO.listarPorUsuario(usuarioId);
    }
    public boolean agregarDireccion(Direcciones direccion) {
        if (direccion == null || direccion.getusuarioId() <= 0) return false;
        
        List <Direcciones> existentes = direccionesDAO.listarPorUsuario(direccion.getusuarioId());
        
        if (existentes == null || existentes.isEmpty()) {
            direccion.setesPrincipal(true);
        } else if (direccion.getesPrincipal()) {
            direccionesDAO.marcarPrincipal(direccion.getusuarioId(), -1);
        }
        return direccionesDAO.crearDireccion(direccion);       
    }
    
    public boolean actualizarDireccion(Direcciones direccion) {
        return direccionesDAO.actualizarDireccion(direccion);
    }
    
    public boolean eliminarDireccion(int idDireccion) {
        return direccionesDAO.borradoDireccionLogico(idDireccion, EstadoDirecciones.Inactiva);
    }
    
    public boolean establecerPrincipal(int usuarioId, int idDireccion) {
        return direccionesDAO.marcarPrincipal(usuarioId, idDireccion);
    }
}