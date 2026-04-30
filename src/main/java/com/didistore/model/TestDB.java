
package com.didistore.model;

import com.didistore.dao.CategoriasDAO;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class TestDB {
    public static void main(String[] args) {
        CategoriasDAO dao = new CategoriasDAO();
        List<Categorias> categorias = dao.listar();
        
        if (categorias.isEmpty()) {
            System.err.println("Conexión exitosa, pero la tabla categorias está vacía. ");
        } else {
            System.err.println("==== LISTADO DE CATEGORÍAS DIDISTORE ====");
            for (Categorias c : categorias) {
                System.out.println("ID: " + c.getid_Categoria() + " | Nombre Categoria: " + c.getnombre_Categoria() + " | Descripción: " + c.getdescripcion() + " | Categoria_Padre_ID: " + c.getcategoria_padre_Id() + " | Fecha Creación: " + c.getfecha_Creacion() );
            }
        }
    }
}