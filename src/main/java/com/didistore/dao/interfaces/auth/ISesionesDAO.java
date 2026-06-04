
package com.didistore.dao.interfaces.auth;

import com.didistore.model.auth.Sesiones;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para el DAO de Sesiones, define los métodos CRUD para la entidad Sesiones
public interface ISesionesDAO {
    
    void insertarSesiones(Sesiones sesion);
    
    String obtenerContrasenaHasheadaPorEmail(String email);
    
    List<Sesiones> listarSesiones();
    
    Sesiones consultarSesiones(int idSesion);
    
    void actualizarSesiones(Sesiones sesion);
    
    void eliminarSesiones(int idSesion);
}