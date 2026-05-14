
package com.didistore.model.modulesalesfinance;

import com.didistore.model.modulesalesfinance.enums.EstadoPedidos;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Pedidos {
    private int idPedido;
    private String numeroPedido;
    private int usuarioId;
    private int direccionEnvioId;
    private EstadoPedidos estadoPedido;
    private double subTotal;
    private double descuento;
    private double iva;
    private double costoEnvio;
    private double montoTotal;
    private int cuponId;
    private Timestamp fechaPedido;
    
    public Pedidos() {}
    
    public Pedidos(int idPedido,
            String numeroPedido,
            int usuarioId,
            int direccionEnvioId,
            EstadoPedidos estadoPedido,
            double subTotal,
            double descuento,
            double iva,
            double costoEnvio,
            double montoTotal,
            int cuponId,
            Timestamp fechaPedido) {
        
        this.idPedido = idPedido;
        this.numeroPedido = numeroPedido;
        this.usuarioId = usuarioId;
        this.direccionEnvioId = direccionEnvioId;
        this.estadoPedido = estadoPedido;
        this.subTotal = subTotal;
        this.descuento = descuento;
        this.iva = iva;
        this.costoEnvio = costoEnvio;
        this.montoTotal = montoTotal;
        this.cuponId = cuponId;
        this.fechaPedido = fechaPedido;
    }
    
    public int getidPedido() { return idPedido; }
    public void setidPedido(int idPedido) { this.idPedido = idPedido; }
    
    public String getnumeroPedido() { return numeroPedido; }
    public void setnumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getdireccionEnvioId() { return direccionEnvioId; }
    public void setdireccionEnvioId(int direccionEnvioId) { this.direccionEnvioId = direccionEnvioId; }
    
    public EstadoPedidos getestadoPedido() { return estadoPedido; }
    public void setestadoPedido(EstadoPedidos estadoPedido) { this.estadoPedido = estadoPedido; }
    
    public double getsubTotal() { return subTotal; }
    public void setsubTotal(double subTotal) { this.subTotal = subTotal; }
    
    public double getdescuento() { return descuento; }
    public void setdescuento(double descuento) { this.descuento = descuento; }
    
    public double getiva() { return iva; }
    public void setiva(double iva) { this.iva = iva; }
    
    public double getcostoEnvio() { return costoEnvio; }
    public void setcostoEnvio(double costoEnvio) { this.costoEnvio = costoEnvio; }
    
    public double getmontoTotal() { return montoTotal; }
    public void setmontoTotal(double montoTotal) { this.montoTotal = montoTotal; }
    
    public int getcuponId() { return cuponId; }
    public void setcuponId(int cuponId) { this.cuponId = cuponId; }
    
    public Timestamp getfechaPedido() { return fechaPedido; }
    public void setfechaPedido(Timestamp fechaPedido) { this.fechaPedido = fechaPedido; }
}