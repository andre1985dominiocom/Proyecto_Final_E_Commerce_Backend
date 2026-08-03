
package com.didistore.model.sales;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa un carrito de compras en el sistema.
public class CarritoCompras {
    private int idCarrito;
    private int usuarioId;
    private String sesionId;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;
    private Timestamp fechaExpiracion;
    
    // Constructor vacío para crear un objeto CarritoCompras sin inicializar sus atributos.
    public CarritoCompras() {}
    
    // Constructor que permite inicializar un objeto CarritoCompras con todos sus atributos.
    public CarritoCompras(int idCarrito,
            int usuarioId,
            String sesionId,
            Timestamp fechaCreacion,
            Timestamp fechaActualizacion,
            Timestamp fechaExpiracion) {
        
        this.idCarrito = idCarrito;
        this.usuarioId = usuarioId;
        this.sesionId = sesionId;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaExpiracion = fechaExpiracion;
    }
    
    // Métodos getter y setter para acceder y modificar los atributos del carrito de compras.
    public int getidCarrito() { return idCarrito; }
    public void setidCarrito(int idCarrito) { this.idCarrito = idCarrito; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public String getsesionId() { return sesionId; }
    public void setsesionId(String sesionId) { this.sesionId = sesionId; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Timestamp getfechaActualizacion() { return fechaActualizacion; }
    public void setfechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    public Timestamp getfechaExpiracion() { return fechaExpiracion; }
    public void setfechaExpiracion(Timestamp fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
}