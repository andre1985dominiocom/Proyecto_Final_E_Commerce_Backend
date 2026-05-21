
package com.didistore.controller.catalog;

import com.didistore.dao.impl.catalog.ProductosDAOImpl;
import com.didistore.dao.interfaces.catalog.IProductosDAO;
import com.didistore.model.catalog.Productos;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ProductosController {

    public static void main(String[] args) {
        
        IProductosDAO productoDAO = new ProductosDAOImpl();
        
        Productos productoConsulta = new Productos();
        productoConsulta.setidProducto(1);
    
        System.out.println("Buscando producto en la BD...");
        Productos producto = productoDAO.consultarProductosPorId(productoConsulta.getidProducto());
        
        if (producto != null) {
            System.out.println("Producto encontrado: " + producto.getidProducto());
        } else {
            System.out.println("Producto no encontrado...");
        }
        System.out.println("¡Proceso de busqueda realizado con éxito!");   
    }
}