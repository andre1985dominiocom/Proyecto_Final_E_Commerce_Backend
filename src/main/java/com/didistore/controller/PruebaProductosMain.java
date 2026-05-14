
package com.didistore.controller;

import com.didistore.dao.moduleinventoryfeedback.IProductosDAO;
import com.didistore.dao.moduleinventoryfeedback.ProductosDAOImpl;
import com.didistore.model.moduleinventoryfeedback.Productos;
import com.didistore.model.moduleinventoryfeedback.enums.EstadoProductos;
import com.didistore.model.moduleinventoryfeedback.enums.TallaProductos;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PruebaProductosMain {

    public static void main(String[] args) {
        
        IProductosDAO productoDAO = new ProductosDAOImpl();
        
        Productos nuevoProducto = new Productos();
        
        nuevoProducto.setnombreProducto("pijama Short");
        nuevoProducto.setdescripcionCorta("Pijama corta de tiras");
        nuevoProducto.setdescripcionLarga("Pijama para mujer short con dibujos de Hello Kitty");
        nuevoProducto.setprecio(30000);
        nuevoProducto.setsku("PIJ-SHORT-001");
        nuevoProducto.settalla(TallaProductos.S);
        nuevoProducto.setcolor("Rosado");
        nuevoProducto.setcategoriaId(1);
        nuevoProducto.setestado(EstadoProductos.Activo);
        nuevoProducto.setesDestacado(1);
        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
        nuevoProducto.setfechaCreacion(fechaActual);
        nuevoProducto.setfechaActualizacion(fechaActual);
        
        System.out.println("Intentado registrar producto en MySQL... ");
        productoDAO.insertarProducto(nuevoProducto);
        System.out.println("¡Proceso de inserción finalizado con éxito!");        
    }
}