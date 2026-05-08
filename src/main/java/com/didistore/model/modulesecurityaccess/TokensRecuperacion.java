
package com.didistore.model.modulesecurityaccess;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class TokensRecuperacion {
    private int idToken;
    private int usuarioId;
    private String tokenHash;
    private Timestamp fechaCreacion;
    private Timestamp fechaExpiracion;
    private int usado;
    private int intentos;
    
    public TokensRecuperacion() {}
    
    public TokensRecuperacion (int idToken,
            int usuarioId,
            String tokenHash,
            Timestamp fechaCreacion,
            Timestamp fechaExpiracion,
            int usado,
            int intentos) {
        
        this.idToken = idToken;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.fechaCreacion = fechaCreacion;
        this.fechaExpiracion = fechaExpiracion;
        this.usado = usado;
        this.intentos = intentos;
    }
    
    public int getidToken() { return idToken; }
    public void setidToken (int idToken) { this.idToken = idToken; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId (int usuarioId) { this.usuarioId = usuarioId; }
    
    public String gettokenHash() { return tokenHash; }
    public void settokenHash (String tokenHash) { this.tokenHash = tokenHash; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion (Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Timestamp getfechaExpiracion() { return fechaExpiracion; }
    public void setfechaExpiracion (Timestamp fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    
    public int getusado() { return usado; }
    public void setusado (int usado) { this.usado = usado; } 
    
    public int getintentos() { return intentos; }
    public void setintentos (int intentos) { this.intentos = intentos; }   
}