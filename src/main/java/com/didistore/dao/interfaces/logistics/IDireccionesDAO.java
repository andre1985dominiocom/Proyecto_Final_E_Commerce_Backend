
package com.didistore.dao.interfaces.logistics;

import com.didistore.model.logistics.Direcciones;
import com.didistore.model.logistics.enums.EstadoDirecciones;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Intefaz para la gestión de direcciones de usuarios en la base de datos.
public interface IDireccionesDAO {
    
    List<Direcciones> listarPorUsuario(int usuarioId);

    Direcciones buscarPorId(int idDireccion);

    boolean crearDireccion(Direcciones direccion);

    boolean actualizarDireccion(Direcciones direccion);

    boolean eliminarDireccion(int idDireccion);

    boolean marcarPrincipal(int usuarioId, int idDireccion);
    
    boolean borradoDireccionLogico(int idDireccion, EstadoDirecciones nuevoEstado);
}