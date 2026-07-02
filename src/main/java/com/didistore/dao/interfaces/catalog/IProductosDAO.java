
package com.didistore.dao.interfaces.catalog;

import com.didistore.model.catalog.Productos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz que define los métodos para la gestión de productos en el catálogo.
public interface IProductosDAO {
    
    void insertarProductos(Productos producto);
    
    List<Productos> listarProductos();
    
    List<Productos> listarProductoPorCategoria(int idCategoria);
    
    Productos consultarProductosPorId(int idProducto);
    
    Productos buscarProductoPorNombre(String nombreProducto);
    
    void actualizarProductos(Productos producto);
    
    void eliminarProductos(int idProducto);
    
    List<Productos> listarCatalogoPublico();
}