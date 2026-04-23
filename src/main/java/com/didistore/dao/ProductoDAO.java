
package com.didistore.dao;

import com.didistore.config.Conexion;
import com.didistore.model.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ProductoDAO {
    
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id_Producto, nombre_Producto, descripcion_corta, descripcion_Larga, precio, sku, talla, color, categoria_Id, estado, es_Destacado, fecha_Creacion, fecha_Actualizacion FROM productos";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
                 
             while (rs.next()) {
                 Producto p = new Producto();
                 p.setid_Producto(rs.getInt("id_Producto"));
                 p.setnombre_Producto(rs.getString("nombre_Producto"));
                 p.setdescripcion_Corta(rs.getString("descripcion_Corta"));
                 p.setdescripcion_Larga(rs.getString("descripcion_Larga"));
                 p.setprecio(rs.getFloat("precio"));
                 p.setsku(rs.getString("sku"));
                 p.settalla(rs.getString("talla"));
                 p.setcolor(rs.getString("color"));
                 p.setcategoria_Id(rs.getInt("categoria_Id"));
                 p.setestado(rs.getString("estado"));
                 p.setes_Destacado(rs.getInt("es_Destacado"));
                 p.setfecha_Creacion(rs.getTimestamp("fecha_creacion"));
                 p.setfecha_Actualizacion(rs.getTimestamp("fecha_Actualizacion"));
                 
                 lista.add(p);
             }
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
                     }
             
        return lista;
   
    }
}
