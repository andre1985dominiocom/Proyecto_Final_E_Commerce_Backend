
package com.didistore.test;

import com.didistore.dao.interfaces.catalog.IProductosDAO;
import com.didistore.dao.impl.catalog.ProductosDAOImpl;
import com.didistore.model.catalog.Productos;
import com.didistore.model.catalog.enums.EstadoProductos;
import com.didistore.model.catalog.enums.TallaProductos;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PruebaProductosMain {

    public static void main(String[] args) {
        
        IProductosDAO productoDAO = new ProductosDAOImpl();
        
        Productos nuevoProducto = new Productos();
        
        nuevoProducto.setnombreProducto("pijama Pantalón Mangas Moñito");
        nuevoProducto.setdescripcionCorta("Pijama pantalón de mangas ");
        nuevoProducto.setdescripcionLarga("Pijama para mujer pantalón de mangas");
        nuevoProducto.setprecio(55000);
        nuevoProducto.setsku("PIJ-PAN-002");
        nuevoProducto.settalla(TallaProductos.S);
        nuevoProducto.setcolor("Rosado");
        nuevoProducto.setcategoriaId(1);
        nuevoProducto.setestado(EstadoProductos.Activo);
        nuevoProducto.setesDestacado(true);
        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
        nuevoProducto.setfechaCreacion(fechaActual);
        nuevoProducto.setfechaActualizacion(fechaActual);
        
        System.out.println("Intentado registrar producto en MySQL... ");
        productoDAO.insertarProductos(nuevoProducto);
        System.out.println("¡Proceso de inserción finalizado con éxito!");        
    }
}