
package com.didistore.model.catalog;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa la entidad Inventarios en el catálogo de productos.
public class Inventarios {
    private int idInventario;
    private int productoId;
    private int stockActual;
    private int stockMinimo;
    private int stockReservado;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;
    
    // Constructor vacío para la clase Inventarios.
    public Inventarios() {}
    
    // Constructor que inicializa todos los atributos de la clase Inventarios.
    public Inventarios (int idInventario,
            int productoId,
            int stockActual,
            int stockMimimo,
            int stockReservado,
            Timestamp fechaCreacion,
            Timestamp fechaActualizacion) {
        
        this.idInventario = idInventario;
        this.productoId = productoId;
        this.stockActual = stockActual;
        this.stockMinimo = stockMimimo;
        this.stockReservado = stockReservado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }
    // Métodos getter y setter para los atributos de la clase Inventarios.
    public int getidInventario() { return idInventario; }
    public void setidInventario(int idInventario) { this.idInventario = idInventario; }
    
    public int getproductoId() { return productoId; }
    public void setproductoId(int productoId) { this.productoId = productoId; }
    
    public int getstockActual() { return stockActual; }
    public void setstockActual(int stockActual) { this.stockActual = stockActual; }
    
    public int getstockMinimo() { return stockMinimo; }
    public void setstockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public int getstockReservado() { return stockReservado; }
    public void setstockReservado(int stockReservado) { this.stockReservado = stockReservado; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Timestamp getfechaActualizacion() { return fechaActualizacion; }
    public void setfechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}