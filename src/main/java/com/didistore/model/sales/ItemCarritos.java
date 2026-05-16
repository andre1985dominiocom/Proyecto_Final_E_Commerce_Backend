
package com.didistore.model.sales;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ItemCarritos {
    private int idItem;
    private int carritoId;
    private int productoId;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private Timestamp fechaAgregado;
    
    public ItemCarritos() {}
    
    public ItemCarritos(int idItem,
            int carritoId,
            int productoId,
            int cantidad,
            double precioUnitario,
            Timestamp fechaAgregado) {
        
        this.idItem = idItem;
        this.carritoId = carritoId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fechaAgregado = fechaAgregado;
        
        this.subtotal = calcularSubtotal();
    }
    private double calcularSubtotal() {
        return this.cantidad * this.precioUnitario;    
    }
    
    public int getidItem() { return idItem; }
    public void setidItem(int id_Item) { this.idItem = id_Item; }
    
    public int getcarritoId() { return carritoId; }
    public void setcarritoId(int carritoId) { this.carritoId = carritoId; }
    
    public int getproductoId() { return productoId; }
    public void setproductoId(int productoId) { this.productoId = productoId; }
    
    public int getcantidad() { return cantidad; }
    public void setcantidad(int cantidad) { this.cantidad = cantidad; this.subtotal = calcularSubtotal(); }
    
    public double getprecioUnitario() { return precioUnitario; }
    public void setprecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; this.subtotal = calcularSubtotal(); }
    
    public double getsubtotal() { return subtotal; }
    
    public Timestamp getfechaAgregado() { return fechaAgregado; }
    public void setfechaAgregado(Timestamp fechaAgregado) { this.fechaAgregado = fechaAgregado; }
}