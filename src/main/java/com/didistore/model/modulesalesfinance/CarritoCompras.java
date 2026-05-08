
package com.didistore.model.modulesalesfinance;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class CarritoCompras {
    private int idCarrito;
    private int usuarioId;
    private String sesion;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;
    private Timestamp fechaExpiracion;
    
    public CarritoCompras() {}
    
    public CarritoCompras(int idCarrito,
            int usuarioId,
            String sesion,
            Timestamp fechaCreacion,
            Timestamp fechaActualizacion,
            Timestamp fechaExpiracion) {
        
        this.idCarrito = idCarrito;
        this.usuarioId = usuarioId;
        this.sesion = sesion;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaExpiracion = fechaExpiracion;
    }
    
    public int getidCarrito() { return idCarrito; }
    public void setidCarrito(int idCarrito) { this.idCarrito = idCarrito; }
    
    public int usuarioId() { return usuarioId; }
    public void setusuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public String getsesion() { return sesion; }
    public void setsesion(String sesion) { this.sesion = sesion; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Timestamp getfechaActualizacion() { return fechaActualizacion; }
    public void setfechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    public Timestamp getfechaExpiracion() { return fechaExpiracion; }
    public void setfechaExpiracion(Timestamp fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
}