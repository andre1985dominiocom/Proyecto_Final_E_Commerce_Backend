
package com.didistore.dao.interfaces.logistics;

import com.didistore.model.logistics.Envios;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para la gestión de Envíos (guías, rastreo, despacho) en la base de datos.
public interface IEnviosDAO {
    
    Envios buscarPorPedido(int pedidoId);
    
    Envios buscarPorId(int idEnvio);
    
    List<Envios> listarPorUsuario(int usuarioId);
    
    boolean crearEnvio(Envios envio);
    
    boolean actualizarEnvio(Envios envio);
    
    boolean actualizarEstado(int idEnvio, String estado);
}
