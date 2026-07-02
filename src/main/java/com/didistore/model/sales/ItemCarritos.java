
package com.didistore.model.sales;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa un item de un carrito de compras
public class ItemCarritos {
    private int idItem;
    private int carritoId;
    private int productoId;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private Timestamp fechaAgregado;
    private String nombreProducto;
    
    // Constructor vacío que permite crear un objeto ItemCarritos sin inicializar sus atributos
    public ItemCarritos() {}
    
    // Constructor que permite crear un objeto ItemCarritos con todos sus atributos inicializados
    public ItemCarritos(int idItem,
            int carritoId,
            int productoId,
            int cantidad,
            double precioUnitario,
            Timestamp fechaAgregado,
            String nombreProducto) {
        
        this.idItem = idItem;
        this.carritoId = carritoId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fechaAgregado = fechaAgregado;
        
        this.subtotal = calcularSubtotal(); // Calcula el subtotal al crear el objeto
        this.nombreProducto = nombreProducto;
    }

    // Método privado que calcula el subtotal del item multiplicando la cantidad por el precio unitario
    private double calcularSubtotal() {
        return this.cantidad * this.precioUnitario;
    }
    
    // Getters y setters para los atributos de la clase ItemCarritos
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
    
    public String getnombreProducto() { return nombreProducto; }
    public void setnombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
}