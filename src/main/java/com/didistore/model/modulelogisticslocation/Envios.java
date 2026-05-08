
package com.didistore.model.modulelogisticslocation;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Envios {
    private int idEnvio;
    private int pedidoId;
    private int direccionId;
    private String transportadora;
    private String numeroGuia;
    private String estado;
    private Timestamp fechaDespacho;
    private Timestamp fechaEntregaEstimada;
    private Timestamp fechaEntregaReal;
    private String observaciones;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;

    public Envios() {}

    public Envios (int idEnvio,
            int pedidoId,
            int direccionId,
            String transportadora,
            String numeroGuia,
            String estado,
            Timestamp fechaDespacho,
            Timestamp fechaEntregaEstimada,
            Timestamp fechaEntregaReal,
            String observaciones,
            Timestamp fechaCreacion,
            Timestamp fechaActualizacion) {
        
        this.idEnvio = idEnvio;
        this.pedidoId = pedidoId;
        this.direccionId = direccionId;
        this.transportadora = transportadora;
        this.numeroGuia = numeroGuia;
        this.estado = estado;
        this.fechaDespacho = fechaDespacho;
        this.fechaEntregaEstimada = fechaEntregaEstimada;
        this.fechaEntregaReal = fechaEntregaReal;
        this.observaciones = observaciones;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public int getidEnvio() { return idEnvio; }
    public void setidEnvio(int idEnvio) { this.idEnvio = idEnvio; }
    
    public int getpedidoId() { return pedidoId; }
    public void setpedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    
    public int getdireccionId() { return direccionId; }
    public void setdireccionId(int direccionId) { this.direccionId = direccionId; }
    
    public String gettransportadora() { return transportadora; }
    public void settransportadora(String transportadora) { this.transportadora = transportadora; }
    
    public String getnumeroGuia() { return numeroGuia; }
    public void setnumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    
    public String getestado() { return estado; }
    public void setestado(String estado) { this.estado = estado; }
    
    public Timestamp getfechaDespacho() { return fechaDespacho; }
    public void setfechaDespacho(Timestamp fechaDespacho) { this.fechaDespacho = fechaDespacho; }
    
    public Timestamp getfechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setfechaEntregaEstimada(Timestamp fechaEntregaEstimada) { this.fechaEntregaEstimada = fechaEntregaEstimada; }
    
    public Timestamp getfechaEntregaReal() { return fechaEntregaReal; }
    public void setfechaEntregaReal(Timestamp fechaEntregaReal) { this.fechaEntregaReal = fechaEntregaReal; }
    
    public String getobservaciones() { return observaciones; }
    public void setobservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Timestamp getfechaActualizacion() { return fechaActualizacion; }
    public void setfechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}