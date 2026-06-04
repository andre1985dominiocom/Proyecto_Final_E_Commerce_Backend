
package com.didistore.dao.interfaces.auth;

import com.didistore.model.auth.Telefonos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para el manejo de los teléfonos asociados a los usuarios en la base de datos.
public interface ITelefonosDAO {
    
    void insertarTelefono(Telefonos telefono);
    
    Telefonos consultarTelefono(int idTelefono);
    
    Telefonos consultarTelefonoPorUsuario (int usuarioId);
    
    List<Telefonos> listarTelefono();
    
    void actualizarTelefono(Telefonos telefono);
    
    void eliminarTelefonoPorUsuario(int usuarioId);
    
    void eliminarTelefonoPorId(int idTelefono); 
}