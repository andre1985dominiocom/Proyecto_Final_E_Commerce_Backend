
package com.didistore.controller.catalog;

import com.didistore.dao.impl.catalog.ProductosDAOImpl;
import com.didistore.dao.interfaces.catalog.IProductosDAO;
import com.didistore.model.catalog.Productos;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ProductosController {

    private final IProductosDAO productosDAO;

    public ProductosController() {
        this.productosDAO = new ProductosDAOImpl();
    }

    public List<Productos> listarProductos() {
        return productosDAO.listarProductos();
    }

    public List<Productos> listarProductoPorCategoria(int idCategoria) {
        if (idCategoria <= 0) {
            return new ArrayList<>();
        }
        return productosDAO.listarProductoPorCategoria(idCategoria);
    }

    public Productos consultarProductosPorId(int idProducto) {
        if (idProducto <= 0) {
            return null;
        }
        return productosDAO.consultarProductosPorId(idProducto);
    }

    public Productos buscarProductosPorNombre(String nombreProducto) {
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            return null;
        }
        return productosDAO.buscarProductoPorNombre(nombreProducto.trim());
    }
    
    public void eliminarProductos(int idProducto) {
        if (idProducto <= 0) {
        }
        productosDAO.eliminarProductos(idProducto);
    }
}