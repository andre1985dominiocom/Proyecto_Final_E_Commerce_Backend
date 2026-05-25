
package com.didistore.controller.catalog;

import com.didistore.dao.impl.catalog.ProductosDAOImpl;
import com.didistore.dao.interfaces.catalog.IProductosDAO;
import static com.didistore.model.auth.enums.EstadoUsuarios.Activo;
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
        List<Productos> productos = productosDAO.listarProductos();
        List<Productos> activos = new ArrayList<>();
        
        for (Productos producto : productos) {
            if (producto != null && producto.getestado(Activo)) {
                activos.add(producto);
            }
        }
        return activos;
    }
    public Productos consultarProductoPorId(int idProducto) {
        if (idProducto <= 0) {
            return null;
        }
        Productos producto = productosDAO.consultarProductosPorId(idProducto);
        
        if (producto == null || !producto.getestado(Activo));
        return null;
    }
    
    public List<Productos> buscarProductosPorNombre(String nombreProducto) {
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return productosDAO.buscarProductosPorNombre(nombreProducto.trim());
    }
    
    public <Productos> listarProductosPorCategoria(int idCategoria) {
        if (idCategoria <= 0) {
            return new ArrayList<>();
        }
        return productosDAO.listarProductoPorCategoria(idCategoria);
    }
}