
package com.didistore.model;

import javax.faces.convert.DateTimeConverter;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Producto {
    private int id_Producto;
    private String nombre_Producto;
    private String descripcion_Corta;
    private String descripcion_Larga;
    private double precio;
    private String sku;
    private Enum talla;
    private String color;
    private int categoria_Id;
    private Enum estado;
    private int es_Destacado;
    private DateTimeConverter fecha_Creacion;
    private DateTimeConverter fecha_Actualizacion;
    
    public Producto() {}
    
    public Producto (int id_Producto, 
            String nombre_Producto, 
            String descripcion_corta, 
            String descripcion_Larga, 
            float precio, 
            String sku, 
            Enum talla, 
            String color,
            int categoria_Id,
            Enum estado, 
            int es_Destacado,
            DateTimeConverter fecha_Creacion,
            DateTimeConverter fecha_Actualizacion) {
    
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
    
    public double getprecio() { return precio; }
    public void setprecio(float precio) { this.precio = precio; }
    
    public String getsku() { return sku; }
    public void setsku(String sku) { this.sku = sku; }
    
    public Enum gettalla() { return talla; }
    public void settalla(Enum talla) { this.talla = talla; }
    
    public String getcolor() { return color; }
    public void setcolor(String color) { this.color = color; }
    
    public int getcategoria_Id() { return categoria_Id; }
    public void setcategoria_Id(int categoria_Id) { this.categoria_Id = categoria_Id; }
    
    public Enum getestado() { return estado; }
    public void setestado(Enum estado) { this.estado = estado; }
    
    public int getes_Destacado() { return es_Destacado; }
    public void setes_Destacado(int es_Destacado) { this.es_Destacado = es_Destacado; }
    
    public DateTimeConverter getfecha_Creacion() { return fecha_Creacion; }
    public void setfecha_Creacion(DateTimeConverter fecha_creacion) { this.fecha_Creacion = fecha_creacion; }
    
    public DateTimeConverter getfecha_Actualizacion() { return fecha_Actualizacion; }
    public void setfecha_Actualizacion(DateTimeConverter fecha_Actualizacion) { this.fecha_Actualizacion = fecha_Actualizacion; }
}
