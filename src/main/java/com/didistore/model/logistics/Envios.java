
package com.didistore.model.logistics;

import com.didistore.model.logistics.enums.EstadoEnvios;
import com.didistore.model.logistics.enums.TransportadoraEnvios;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa un envío de un pedido a una dirección específica,
// incluyendo información sobre la transportadora, número de guía, estado del envío y fechas relevantes.
public class Envios {
    private int idEnvio;
    private int pedidoId;
    private int direccionId;
    private TransportadoraEnvios transportadora;
    private String numeroGuia;
    private EstadoEnvios estado;
    private Timestamp fechaDespacho;
    private Timestamp fechaEntregaEstimada;
    private Timestamp fechaEntregaReal;
    private String observaciones;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;

    // Constructor vacío para la clase Envios
    public Envios() {}

    // Constructor completo para la clase Envios, que inicializa todos los atributos de la clase.
    public Envios (int idEnvio,
            int pedidoId,
            int direccionId,
            TransportadoraEnvios transportadora,
            String numeroGuia,
            EstadoEnvios estado,
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
    
    // Getters y Setters para los atributos de la clase Envios
    public int getidEnvio() { return idEnvio; }
    public void setidEnvio(int idEnvio) { this.idEnvio = idEnvio; }
    
    public int getpedidoId() { return pedidoId; }
    public void setpedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    
    public int getdireccionId() { return direccionId; }
    public void setdireccionId(int direccionId) { this.direccionId = direccionId; }
    
    public TransportadoraEnvios gettransportadora() { return transportadora; }
    public void settransportadora(TransportadoraEnvios transportadora) { this.transportadora = transportadora; }
    
    public String getnumeroGuia() { return numeroGuia; }
    public void setnumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    
    public EstadoEnvios getestado() { return estado; }
    public void setestado(EstadoEnvios estado) { this.estado = estado; }
    
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