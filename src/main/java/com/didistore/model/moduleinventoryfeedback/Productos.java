
package com.didistore.model.moduleinventoryfeedback;

import com.didistore.util.EstadoProductos;
import com.didistore.util.TallaProductos;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Productos {
    private int idProducto;
    private String nombreProducto;
    private String descripcionCorta;
    private String descripcionLarga;
    private float precio;
    private String sku;
    private TallaProductos talla;
    private String color;
    private int categoriaId;
    private EstadoProductos estado;
    private int esDestacado;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;
    
    public Productos() {}
    
    public Productos (int idProducto,
            String nombreProducto,
            String descripcioncorta,
            String descripcionLarga,
            float precio,
            String sku,
            TallaProductos talla,
            String color,
            int categoriaId,
            EstadoProductos estado,
            int esDestacado,
            Timestamp fechaCreacion,
            Timestamp fechaActualizacion) {
    
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionCorta = descripcioncorta;
        this.descripcionCorta = descripcionLarga;
        this.precio = precio;
        this.sku = sku;
        this.talla = talla;
        this.color = color;
        this.categoriaId = categoriaId;
        this.estado = estado;
        this.esDestacado = esDestacado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
}
    
    public int getidProducto() { return idProducto; }
    public void setidProducto(int idProducto) { this.idProducto = idProducto; }
    
    public String getnombreProducto() { return nombreProducto; }
    public void setnombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    
    public String getdescripcionCorta() { return descripcionCorta; }
    public void setdescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }
    
    public String getdescripcionLarga() { return descripcionLarga; }
    public void setdescripcionLarga(String descripcionLarga) { this.descripcionLarga = descripcionLarga; }
    
    public float getprecio() { return precio; }
    public void setprecio(float precio) { this.precio = precio; }
    
    public String getsku() { return sku; }
    public void setsku(String sku) { this.sku = sku; }
    
    public TallaProductos gettalla() { return talla; }
    public void settalla(TallaProductos talla) { this.talla = talla; }
    
    public String getcolor() { return color; }
    public void setcolor(String color) { this.color = color; }
    
    public int getcategoriaId() { return categoriaId; }
    public void setcategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
    
    public EstadoProductos getestado() { return estado; }
    public void setestado(EstadoProductos estado) { this.estado = estado; }
    
    public int getesDestacado() { return esDestacado; }
    public void setesDestacado(int esDestacado) { this.esDestacado = esDestacado; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Timestamp getfechaActualizacion() { return fechaActualizacion; }
    public void setfechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
