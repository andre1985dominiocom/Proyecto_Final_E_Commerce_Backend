
package com.didistore.model.modulesalesfinance;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class HistorialEstadoPedidos {
    private int idHistorial;
    private int pedidoId;
    private String estadoAnterior;
    private String estadoNuevo;
    private int usuarioId;
    private Timestamp fechaCambio;
    private String notas;
    
    public HistorialEstadoPedidos() {}
    
    public HistorialEstadoPedidos(int idHistorial,
            int pedidoId,
            String estadoAnterior,
            String estadoNuevo,
            int usuarioId,
            Timestamp fechaCambio,
            String notas) {
        
        this.idHistorial = idHistorial;
        this.pedidoId = pedidoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.usuarioId = usuarioId;
        this.fechaCambio = fechaCambio;
        this.notas = notas;
    }
    
    public int getidHistorial() { return idHistorial; }
    public void setidHistorial(int idHistorial) { this.idHistorial = idHistorial; }
    
    public int getpedidoId() { return pedidoId; }
    public void setpedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    
    public String getestadoAnterior() { return estadoAnterior; }
    public void setestadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    
    public String getestadoNuevo() { return estadoNuevo; }
    public void setestadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public Timestamp getfechaCambio() { return fechaCambio; }
    public void setfechaCambio(Timestamp fechaCambio) { this.fechaCambio = fechaCambio; }
    
    public String getnotas() { return notas; }
    public void setnotas(String notas) { this.notas = notas; }
}