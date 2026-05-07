
package com.didistore.model.moduleinventoryfeedback;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Productos {
    private int id_Producto;
    private String nombre_Producto;
    private String descripcion_Corta;
    private String descripcion_Larga;
    private float precio;
    private String sku;
    private String talla;
    private String color;
    private int categoria_Id;
    private String estado;
    private int es_Destacado;
    private Timestamp fecha_Creacion;
    private Timestamp fecha_Actualizacion;
    
    public Productos() {}
    
    public Productos (int id_Producto,
            String nombre_Producto,
            String descripcion_corta,
            String descripcion_Larga,
            float precio,
            String sku,
            String talla,
            String color,
            int categoria_Id,
            String estado,
            int es_Destacado,
            Timestamp fecha_Creacion,
            Timestamp fecha_Actualizacion) {
    
        this.id_Producto = id_Producto;
        this.nombre_Producto = nombre_Producto;
        this.descripcion_Corta = descripcion_Larga;
        this.precio = precio;
        this.sku = sku;
        this.talla = talla;
        this.color = color;
        this.categoria_Id = categoria_Id;
        this.estado = estado;
        this.es_Destacado = es_Destacado;
        this.fecha_Creacion = fecha_Creacion;
        this.fecha_Actualizacion = fecha_Actualizacion;
}
    
    public int getid_Producto() { return id_Producto; }
    public void setid_Producto(int id_Producto) { this.id_Producto = id_Producto; }
    
    public String getnombre_Producto() { return nombre_Producto; }
    public void setnombre_Producto(String nombre_Producto) { this.nombre_Producto = nombre_Producto; }
    
    public String getdescripcion_Corta() { return descripcion_Corta; }
    public void setdescripcion_Corta(String descripcion_Corta) { this.descripcion_Corta = descripcion_Corta; }
    
    public String getdescripcion_Larga() { return descripcion_Larga; }
    public void setdescripcion_Larga(String descripcion_Larga) { this.descripcion_Larga = descripcion_Larga; }
    
    public float getprecio() { return precio; }
    public void setprecio(float precio) { this.precio = precio; }
    
    public String getsku() { return sku; }
    public void setsku(String sku) { this.sku = sku; }
    
    public String gettalla() { return talla; }
    public void settalla(String talla) { this.talla = talla; }
    
    public String getcolor() { return color; }
    public void setcolor(String color) { this.color = color; }
    
    public int getcategoria_Id() { return categoria_Id; }
    public void setcategoria_Id(int categoria_Id) { this.categoria_Id = categoria_Id; }
    
    public String getestado() { return estado; }
    public void setestado(String estado) { this.estado = estado; }
    
    public int getes_Destacado() { return es_Destacado; }
    public void setes_Destacado(int es_Destacado) { this.es_Destacado = es_Destacado; }
    
    public Timestamp getfecha_Creacion() { return fecha_Creacion; }
    public void setfecha_Creacion(Timestamp fecha_creacion) { this.fecha_Creacion = fecha_creacion; }
    
    public Timestamp getfecha_Actualizacion() { return fecha_Actualizacion; }
    public void setfecha_Actualizacion(Timestamp fecha_Actualizacion) { this.fecha_Actualizacion = fecha_Actualizacion; }
}
