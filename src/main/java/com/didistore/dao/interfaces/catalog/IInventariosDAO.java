
package com.didistore.dao.interfaces.catalog;

import com.didistore.model.catalog.Inventarios;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para la gestión de inventarios en la base de datos
public interface IInventariosDAO {
    
    List<Inventarios> listarInventario();
    
    List<Inventarios> listarPorProducto(int productoId);
    
    Inventarios consultarInventarioPorId(int idInventario);
    
    boolean insertarInventario(Inventarios inventario);
    
    boolean actualizarInventario(Inventarios inventario);
    
    boolean eliminarInventario(int idInventario);
    
    int obtenerStockDisponible(int productoId);
    
    boolean hayStockSuficiente(int productoId, int cantidad); 
    
    boolean descontarStock(int productoId, int cantidad);
}