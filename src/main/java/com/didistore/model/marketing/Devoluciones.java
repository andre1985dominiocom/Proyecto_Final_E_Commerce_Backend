
package com.didistore.model.marketing;

import com.didistore.model.marketing.enums.EstadoDevoluciones;
import com.didistore.model.marketing.enums.MotivoDevoluciones;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvaez Lache
 */
public class Devoluciones {
    private int idDevolucion;
    private int pedidoId;
    private int usuarioId;
    private MotivoDevoluciones motivo;
    private String descripcion;
    private EstadoDevoluciones estado;
    private Timestamp fechaSolicitud;
    private Timestamp fechaResolucion;
    private double montoReembolso;
    
    public Devoluciones() {}
    
    public Devoluciones (int idDevolucion,
            int pedidoId,
            int usuarioId,
            MotivoDevoluciones motivo,
            String descripcion,
            EstadoDevoluciones estado,
            Timestamp fechaSolicitud,
            Timestamp fechaResolucion,
            double montoReembolso) {
        
        this.idDevolucion = idDevolucion;
        this.pedidoId = pedidoId;
        this.usuarioId = usuarioId;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaResolucion = fechaResolucion;
        this.montoReembolso = montoReembolso;
    }
    
    public int getidDevolucion() { return idDevolucion; }
    public void setidDevolucion(int idDevolucion) { this.idDevolucion = idDevolucion; }
    
    public int getpedidoId() { return pedidoId; }
    public void setpedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public MotivoDevoluciones getmotivo() { return motivo; }
    public void setmotivo(MotivoDevoluciones motivo) { this.motivo = motivo; }
    
    public String getdescripcion() { return descripcion; }
    public void setdescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public EstadoDevoluciones getestado() { return estado; }
    public void setestado(EstadoDevoluciones estado) { this.estado = estado; }
    
    public Timestamp getfechaSolicitud() { return fechaSolicitud; }
    public void setfechaSolicitud(Timestamp fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    
    public Timestamp getfechaResolucion() { return fechaResolucion; }
    public void setfechaResolucion(Timestamp fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    
    public double getmontoReembolso() { return montoReembolso; }
    public void setmontoReembolso(double montoReembolso) { this.montoReembolso = montoReembolso; }
}