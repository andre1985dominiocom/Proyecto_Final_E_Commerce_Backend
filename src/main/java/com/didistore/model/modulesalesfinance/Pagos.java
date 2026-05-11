
package com.didistore.model.modulesalesfinance;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Pagos {
    private int idPago;
    private int pedidoId;
    private String metodoPago;
    private String estadoPago;
    private double monto;
    private String referenciaTransaccion;
    private String referenciaInterna;
    private String datosPasarela;
    private Timestamp fechaPago;
    private Timestamp fechaCreacion;
    
    public Pagos() {}
    
    public Pagos(int idPago,
            int pedidoId,
            String metodoPago,
            String estadoPago,
            double monto,
            String referenciaTransaccion,
            String referenciaInterna,
            String datosPasarela,
            Timestamp fechaPago,
            Timestamp fechaCreacion) {
        
        this.idPago = idPago;
        this.pedidoId = pedidoId;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
        this.monto = monto;
        this.referenciaTransaccion = referenciaTransaccion;
        this.referenciaInterna = referenciaInterna;
        this.datosPasarela = datosPasarela;
        this.fechaPago = fechaPago;
        this.fechaCreacion = fechaCreacion;
    }
    
    public int getidPago() { return idPago; }
    public void setidPago(int idPago) { this.idPago = idPago; }
    
    public int getpedidoId() { return pedidoId; }
    public void setpedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    
    public String getmetodoPago() { return metodoPago; }
    public void setmetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public String getestadoPago() { return estadoPago; }
    public void setestadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    
    public double getmonto() { return monto; }
    public void setmonto(double monto) { this.monto = monto; }
    
    public String getreferenciaTransaccion() { return referenciaTransaccion; }
    public void setreferenciaTransaccion(String referenciaTransaccion) { this.referenciaTransaccion = referenciaTransaccion; }
    
    public String getreferenciaInterna() { return referenciaInterna; }
    public void setreferenciaInterna(String referenciaInterna) { this.referenciaInterna = referenciaInterna; }
    
    public String getdatosPasarela() { return datosPasarela; }
    public void setdatosPasarela(String datosPasarela) { this.datosPasarela = datosPasarela; }
    
    public Timestamp getfechaPago() { return fechaPago; }
    public void setfechaPago(Timestamp fechaPago) { this.fechaPago = fechaPago; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}