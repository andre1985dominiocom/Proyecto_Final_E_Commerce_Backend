package com.didistore.test;

import com.didistore.dao.interfaces.catalog.CategoriasDAO;
import com.didistore.model.catalog.Categorias;
import java.util.Map;

/**
 *, 
 * @author Sergio Andrés Álvarez Lache
 */
public class TestDB {

    public static void main(String[] args) {
        CategoriasDAO dao = new CategoriasDAO();
        java.util.Map<String, String[]> estructura = new java.util.LinkedHashMap<>();
    
        estructura.put("Pijamas de Mujer", new String[]{"Pijamas Pantalón", "Pijamas Short", "Batas"});
        estructura.put("Accesorios", new String[]{"Organizadores", "Cosmetiqueras"});
        estructura.put("Complementos", new String[]{"Bolsos", "Morrales"});
    
        try {
            System.out.println("=== INICIANDO CARGA INICIAL DIDISTORE ===");
        
            for (Map.Entry<String, String[]> entrada : estructura.entrySet()) {
                Categorias raiz = new Categorias();
                raiz.setnombreCategoria(entrada.getKey());
                raiz.setdescripcion("Categoría principal de " + entrada.getKey());
                raiz.setcategoriaPadreId(null);
            
                int idRaizGenerado = dao.insertarCategorias(raiz);
            
                if (idRaizGenerado > 0) {
                
                    for (String nombreSub : entrada.getValue()) {
                        Categorias sub = new Categorias();
                        sub.setnombreCategoria(nombreSub);
                        sub.setdescripcion("Subcategoría de " + entrada.getKey());
                          
                        sub.setcategoriaPadreId(idRaizGenerado); 
                        
                        dao.insertarCategorias(sub);
                    }
                }
            }
            System.out.println("\n=== CARGA FINALIZADA CON ÉXITO ===");
        
            } catch (java.sql.SQLException e) {
                System.err.println("Error de SQL: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
        }
    }
}