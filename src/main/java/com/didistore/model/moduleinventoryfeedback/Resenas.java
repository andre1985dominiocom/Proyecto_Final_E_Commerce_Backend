
package com.didistore.model.moduleinventoryfeedback;

import com.didistore.model.moduleinventoryfeedback.enums.EstadoResenas;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Resenas {
    private int idResena;
    private int usuarioId;
    private int productoId;
    private int calificacion;
    private String comentario;
    private EstadoResenas estado;
    
    public Resenas() {}
    
    public Resenas(int idResena,
            int usuarioId,
            int productoId,
            int calificacion,
            String comentario,
            EstadoResenas estado) {
        
        this.idResena = idResena;
        this.usuarioId = usuarioId;
        this.productoId = productoId;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.estado = estado;        
    }
    
    public int getidResena() { return idResena; }
    public void setidResena(int idResena) { this.idResena = idResena; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getproductoId() { return productoId; }
    public void setproductoId(int productoId) { this.productoId = productoId; }
    
    public int getcalificacion() { return calificacion; }
    public void setcalificacion(int calificacion) { this.calificacion = calificacion; }
    
    public String getcomentario() { return comentario; }
    public void setcomentario(String comentario) { this.comentario = comentario; }
    
    public EstadoResenas getestado() { return estado; }
    public void setestado(EstadoResenas estado) { this.estado =  estado; }
}