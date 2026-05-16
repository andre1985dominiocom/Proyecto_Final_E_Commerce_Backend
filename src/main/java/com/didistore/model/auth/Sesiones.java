
package com.didistore.model.auth;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Sesiones {
    private int idSesion;
    private int usuarioId;
    private String tokenSesion;
    private Timestamp fechaCreacion;
    private Timestamp fechaExpiracion;
    private String ip;
    private String userAgent;
    private int revocada;
    
    public Sesiones() {}
    
    public Sesiones (int idSesion,
            int usuarioId,
            String tokenSesion,
            Timestamp fechaCreacion,
            Timestamp fechaExpiracion,
            String ip,
            String userAgent,
            int revocada) {
        
        this.idSesion = idSesion;
        this.usuarioId = usuarioId;
        this.tokenSesion = tokenSesion;
        this.fechaCreacion = fechaCreacion;
        this.fechaExpiracion = fechaExpiracion;
        this.ip = ip;
        this.userAgent = userAgent;
        this.revocada = revocada;
    }
    
    public int getidSesion() { return idSesion; }
    public void setidSesion (int idSesion) { this.idSesion = idSesion; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId (int usuarioId) { this.usuarioId = usuarioId; }
    
    public String gettokenSesion() { return tokenSesion; }
    public void settokenSesion (String tokenSesion) { this.tokenSesion = tokenSesion; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion (Timestamp fecha_Creacion) { this.fechaCreacion = fecha_Creacion; }
    
    public Timestamp getfechaExpiracion() { return fechaExpiracion; }
    public void setfechaExpiracion (Timestamp fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    
    public String getip() { return ip; }
    public void setip (String ip) { this.ip = ip; } 
    
    public String getuserAgent() { return userAgent; }
    public void setuserAgent (String userAgent) { this.userAgent = userAgent; }  
    
    public int getrevocada() { return revocada; }
    public void setrevocada (int revocada) { this.revocada = revocada; }    
}