
package com.didistore.model.modulelogisticslocation;

import com.didistore.model.modulelogisticslocation.enums.EstadoDirecciones;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Direcciones {
    private int idDireccion;
    private String direccion;
    private int esPrincipal;
    private String barrio;
    private String referencia;
    private int ciudadId;
    private int usuarioId;
    private EstadoDirecciones estado;
    private Timestamp fechaCreacion;
    
    public Direcciones() {}
    
    public Direcciones (int idDireccion,
            String direccion,
            int esPrincipal,
            String barrio,
            String referencia,
            int ciudadId,
            int usuarioId,
            EstadoDirecciones estado,
            Timestamp fechaCreacion) {
        
        this.idDireccion = idDireccion;
        this.direccion = direccion;
        this.esPrincipal = esPrincipal;
        this.barrio = barrio;
        this.referencia = referencia;
        this.ciudadId = ciudadId;
        this.usuarioId = usuarioId;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }
    
    public int getidDireccion() { return idDireccion; }
    public void setidDireccion(int idDireccion) { this.idDireccion = idDireccion; }
    
    public String getdireccion() { return direccion; }
    public void setdireccion(String direccion) { this.direccion = direccion; }
    
    public int getesPrincipal() { return esPrincipal; }
    public void setesPrincipal(int esPrincipal) { this.esPrincipal = esPrincipal; }
    
    public String getbarrio() { return barrio; }
    public void setbarrio(String barrio) { this.barrio = barrio; }
    
    public String getreferencia() { return referencia; }
    public void setreferencia(String referencia) { this.referencia = referencia; }
    
    public int getciudadId() { return ciudadId; }
    public void setciudadId(int ciudadId) { this.ciudadId = ciudadId; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public EstadoDirecciones getestado() { return estado; }
    public void setestado(EstadoDirecciones estado) { this.estado = estado; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}