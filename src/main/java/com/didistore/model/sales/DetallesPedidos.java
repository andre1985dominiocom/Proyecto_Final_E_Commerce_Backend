
package com.didistore.model.sales;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa los detalles de un pedido, incluyendo información sobre el producto,
// cantidad, precio unitario y subtotal.
public class DetallesPedidos {
    private int idDetalle;
    private int pedidoId;
    private int productoId;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    
    // Constructor vacío para la clase DetallesPedidos.
    public DetallesPedidos() {}
    
    // Constructor que inicializa los atributos de la clase DetallesPedidos con los valores proporcionados.
    public DetallesPedidos (int idDetalle,
            int pedidoId,
            int productoId,
            int cantidad,
            double precioUnitario,
            double subtotal) {
        
        this.idDetalle = idDetalle;
        this.pedidoId = pedidoId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }
    
    // Métodos getter y setter para los atributos de la clase DetallesPedidos.
    public int getidDetalle() { return idDetalle; }
    public void setidDetalle(int idDetalle) { this.idDetalle = idDetalle; }
    
    public int getpedidoId() { return pedidoId; }
    public void setpedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    
    public int getproductoId() { return productoId; }
    public void setproductoId(int productoId) { this.productoId = productoId; }
    
    public int getcantidad() { return cantidad; }
    public void setcantidad(int cantidad) { this.cantidad = cantidad; }
    
    public double getprecioUnitario() { return precioUnitario; }
    public void setprecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public double getsubtotal() { return subtotal; }
    public void setsubtotal(double subtotal) { this.subtotal = subtotal; }
}