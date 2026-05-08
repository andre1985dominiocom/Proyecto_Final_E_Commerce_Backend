
package com.didistore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.didistore.config.Conexion;
import com.didistore.model.moduleinventoryfeedback.Productos;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ProductosDAO {
    
    public void insertarProductos(Productos producto) {
        
        String sql = "INSERT INTO Productos (nombre_Producto, "
                + "descripcion_Corta, "
                + "descripcion_Larga, "
                + "precio, "
                + "sku, "
                + "talla, "
                + "color, "
                + "categoria_Id, "
                + "estado, "
                + "es_Destacado, "
                + "fecha_Creacion, "
                + "fecha_Actualizacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    
    
    public List<Productos> listar() {
        List<Productos> lista = new ArrayList<>();
        String sql = "SELECT id_Producto, "
                + "nombre_Producto, "
                + "descripcion_corta, "
                + "descripcion_Larga, "
                + "precio, "
                + "sku, "
                + "talla, "
                + "color, "
                + "categoria_"
                + "Id, "
                + "estado, "
                + "es_Destacado, "
                + "fecha_Creacion, "
                + "fecha_Actualizacion FROM productos";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                
            while (rs.next()) {
                Productos p = new Productos();
                p.setidProducto(rs.getInt("id_Producto"));
                p.setnombreProducto(rs.getString("nombre_Producto"));
                p.setdescripcionCorta(rs.getString("descripcion_Corta"));
                p.setdescripcionLarga(rs.getString("descripcion_Larga"));
                p.setprecio(rs.getFloat("precio"));
                p.setsku(rs.getString("sku"));
                p.settalla(rs.getString("talla"));
                p.setcolor(rs.getString("color"));
                p.setcategoriaId(rs.getInt("categoria_Id"));
                p.setestado(rs.getString("estado"));
                p.setesDestacado(rs.getInt("es_Destacado"));
                p.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                p.setfechaActualizacion(rs.getTimestamp("fecha_Actualizacion"));
                
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
                    }
            
        return lista;
    }
}
