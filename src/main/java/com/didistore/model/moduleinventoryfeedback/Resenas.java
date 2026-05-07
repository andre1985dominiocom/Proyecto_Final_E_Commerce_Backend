
package com.didistore.model.moduleinventoryfeedback;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Resenas {
    private int id_Resena;
    private int usuario_Id;
    private int producto_Id;
    private int calificacion;
    private String comentario;
    private String estado;
    
    public Resenas() {}
    
    public Resenas(int id_Resena,
            int usuario_Id,
            int producto_Id,
            int calificacion,
            String comentario,
            String estado) {
        
        this.id_Resena = id_Resena;
        this.usuario_Id = usuario_Id;
        this.producto_Id = producto_Id;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.estado = estado;        
    }
    
    public int getid_Resena() { return id_Resena; }
    public void setid_Resena(int id_Resena) { this.id_Resena = id_Resena; }
    
    public int getusuario_Id() { return usuario_Id; }
    public void setusuario_Id(int usuario_Id) { this.usuario_Id = usuario_Id; }
    
    public int getproducto_Id() { return producto_Id; }
    public void setproducto_Id(int producto_Id) { this.producto_Id = producto_Id; }
    
    public int getcalificacion() { return calificacion; }
    public void setcalificacion(int calificacion) { this.calificacion = calificacion; }
    
    public String getcomentario() { return comentario; }
    public void setcomentario(String comentario) { this.comentario = comentario; }
    
    public String getestado() { return estado; }
    public void setestado(String estado) { this.estado =  estado; }
}