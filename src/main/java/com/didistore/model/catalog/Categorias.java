
package com.didistore.model.catalog;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Categorias {
    private int idCategoria;
    private String nombreCategoria;
    private String descripcion;
    private Integer categoriaPadreId;
    private Timestamp fechaCreacion;
    
    public Categorias() {}
    
    public Categorias (int idCategoria,
            String nombreCategoria,
            String descripcion,
            Integer categoriaPadreId,
            Timestamp fechaCreacion) {
        
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
        this.categoriaPadreId = categoriaPadreId;
        this.fechaCreacion = fechaCreacion;
    }
    
    public int getidCategoria() { return idCategoria; }
    public void setidCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    
    public String getnombreCategoria() { return nombreCategoria; }
    public void setnombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
    
    public String getdescripcion() { return  descripcion; }
    public void setdescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Integer getcategoriaPadreId() { return categoriaPadreId; }
    public void setcategoriaPadreId(Integer categoriaPadreId) { this.categoriaPadreId = categoriaPadreId; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}