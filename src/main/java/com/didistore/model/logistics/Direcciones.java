
package com.didistore.model.logistics;

import com.didistore.model.logistics.enums.EstadoDirecciones;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa la entidad Direcciones
public class Direcciones {
    private int idDireccion;
    private String direccion;
    private boolean esPrincipal;
    private String barrio;
    private String referencia;
    private int ciudadId;
    private int usuarioId;
    private EstadoDirecciones estado;
    private Timestamp fechaCreacion;
    
    // Constructor vacío que permite crear un objeto Direcciones sin inicializar sus atributos
    public Direcciones() {}
    
    // Constructor que permite crear un objeto Direcciones con todos sus atributos inicializados
    public Direcciones (int idDireccion,
            String direccion,
            boolean esPrincipal,
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
    
    // Métodos getter y setter para cada atributo de la clase Direcciones
    public int getidDireccion() { return idDireccion; }
    public void setidDireccion(int idDireccion) { this.idDireccion = idDireccion; }
    
    public String getdireccion() { return direccion; }
    public void setdireccion(String direccion) { this.direccion = direccion; }
    
    public boolean getesPrincipal() { return esPrincipal; }
    public void setesPrincipal(boolean esPrincipal) { this.esPrincipal = esPrincipal; }
    
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