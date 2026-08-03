
package com.didistore.controller.admin;

import com.didistore.dao.impl.catalog.ProductosDAOImpl;
import com.didistore.dao.interfaces.catalog.IProductosDAO;
import com.didistore.model.catalog.Productos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Controlador para la gestión de productos en el panel de administración
public class AdminProductosController {
    
    private final IProductosDAO productosDAO;

    public AdminProductosController() {
        this.productosDAO = new ProductosDAOImpl();
    }

    public void insertarProductos(Productos producto) {
        productosDAO.insertarProductos(producto);
    }

    public List<Productos> listarProductos() {
        return productosDAO.listarProductos();
    }

    public Productos consultarProductosPorId(int idProducto) {
        return productosDAO.consultarProductosPorId(idProducto);
    }

    public Productos buscarProductosPorNombre(String nombreProducto) {
        return productosDAO.buscarProductoPorNombre(nombreProducto);
    }

    public void actualizarProductos(Productos producto) {
        productosDAO.actualizarProductos(producto);
    }

    public void eliminarProductos(int idProducto) {
        productosDAO.eliminarProductos(idProducto);
    }
}