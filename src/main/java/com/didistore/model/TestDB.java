
package com.didistore.model;

import com.didistore.dao.ProductoDAO;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class TestDB {
    public static void main(String[] args) {
        ProductoDAO dao = new ProductoDAO();
        List<Productos> productos = dao.listar();
        
        if (productos.isEmpty()) {
            System.err.println("Conexión exitosa, pero la tabla productos está vacía. ");
        } else {
            System.err.println("==== LISTADO DE PRODUCTOS DIDISTORE ====");
            for (Productos p : productos) {
                System.out.println("ID: " + p.getid_Producto() + " | Nombre producto: " + p.getnombre_Producto() + " | Precio producto: $" + p.getprecio());
            }
        }
    }
}