
package com.didistore.model;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Categorias {
    private int id_Categoria;
    private String nombre_Categoria;
    private String descripcion;
    private Integer categoria_padre_Id;
    private Timestamp fecha_Creacion;
    
    public Categorias() {}
    
    public Categorias (int id_Categoria,
            String nombre_Categoria,
            String descripcion,
            Integer categoria_padre_Id,
            Timestamp fecha_creacion) {
        
        this.id_Categoria = id_Categoria;
        this.nombre_Categoria = nombre_Categoria;
        this.descripcion = descripcion;
        this.categoria_padre_Id = categoria_padre_Id;
        this.fecha_Creacion = fecha_creacion;
    }
    
    public int getid_Categoria() { return id_Categoria; }
    public void setid_Categoria(int id_Categoria) { this.id_Categoria = id_Categoria; }
    
    public String getnombre_Categoria() { return nombre_Categoria; }
    public void setnombre_Categoria(String nombre_Categoria) { this.nombre_Categoria = nombre_Categoria; }
    
    public String getdescripcion() { return  descripcion; }
    public void setdescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Integer getcategoria_padre_Id() { return categoria_padre_Id; }
    public void setcategoria_padre_id(Integer categoria_padre_Id) { this.categoria_padre_Id = categoria_padre_Id; }
    
    public Timestamp getfecha_Creacion() { return fecha_Creacion; }
    public void setfecha_Creacion(Timestamp fecha_creacion) { this.fecha_Creacion = fecha_Creacion; }

    public void setcategoria_padre_Id(Integer categoria_padre_Id) {
        this.categoria_padre_Id = categoria_padre_Id;
    }
}