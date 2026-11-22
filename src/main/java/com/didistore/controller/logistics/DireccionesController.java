
package com.didistore.controller.logistics;

import com.didistore.dao.impl.logistics.DireccionesDAOImpl;
import com.didistore.dao.interfaces.logistics.IDireccionesDAO;
import com.didistore.model.logistics.Direcciones;
import com.didistore.model.logistics.enums.EstadoDirecciones;
import com.didistore.util.ValidationUtil;
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
        
        // Validación de formato colombiano (CU-CLI-03 paso 3)
        if (!validarDireccion(direccion)) return false;
        
        List <Direcciones> existentes = direccionesDAO.listarPorUsuario(direccion.getusuarioId());
        
        if (existentes == null || existentes.isEmpty()) {
            direccion.setesPrincipal(true);
        } else if (direccion.getesPrincipal()) {
            direccionesDAO.marcarPrincipal(direccion.getusuarioId(), -1);
        }
        return direccionesDAO.crearDireccion(direccion);       
    }
    
    public boolean actualizarDireccion(Direcciones direccion) {
        if (direccion == null) return false;
        if (!validarDireccion(direccion)) return false;
        return direccionesDAO.actualizarDireccion(direccion);
    }

    /**
     * Valida dirección colombiana, barrio y ciudad existente.
     */
    public boolean validarDireccion(Direcciones direccion) {
        if (!ValidationUtil.direccionColombianaValida(direccion.getdireccion())) {
            return false;
        }
        if (!ValidationUtil.barrioValido(direccion.getbarrio())) {
            return false;
        }
        if (direccion.getciudadId() <= 0) {
            return false;
        }
        return true;
    }

    /**
     * Devuelve mensaje de error legible para la validación (o null si es válida).
     */
    public String mensajeValidacion(Direcciones direccion) {
        if (direccion == null) return "Dirección no válida";
        if (!ValidationUtil.direccionColombianaValida(direccion.getdireccion())) {
            return "La dirección debe tener formato colombiano (ej: Calle 123 # 45-67)";
        }
        if (!ValidationUtil.barrioValido(direccion.getbarrio())) {
            return "El barrio no es válido";
        }
        if (direccion.getciudadId() <= 0) {
            return "Debes seleccionar una ciudad";
        }
        return null;
    }
    
    public boolean eliminarDireccion(int idDireccion) {
        return direccionesDAO.borradoDireccionLogico(idDireccion, EstadoDirecciones.Inactiva);
    }
    
    public boolean establecerPrincipal(int usuarioId, int idDireccion) {
        return direccionesDAO.marcarPrincipal(usuarioId, idDireccion);
    }
}