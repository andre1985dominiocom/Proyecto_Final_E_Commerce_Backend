
package com.didistore.model.auth;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa un token de recuperación de contraseña, con sus atributos y métodos de acceso.
public class TokensRecuperacion {
    private int idToken;
    private int usuarioId;
    private String tokenHash;
    private Timestamp fechaCreacion;
    private Timestamp fechaExpiracion;
    private boolean usado;
    private int intentos;
    
    // Constructor vacío y constructor con parámetros para inicializar los atributos.
    public TokensRecuperacion() {}
    
    // Constructor con parámetros para inicializar los atributos del token de recuperación.
    public TokensRecuperacion (int idToken,
            int usuarioId,
            String tokenHash,
            Timestamp fechaCreacion,
            Timestamp fechaExpiracion,
            boolean usado,
            int intentos) {
        
        // Inicialización de los atributos del token de recuperación con los valores proporcionados.
        this.idToken = idToken;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.fechaCreacion = fechaCreacion;
        this.fechaExpiracion = fechaExpiracion;
        this.usado = usado;
        this.intentos = intentos;
    }
    
    // Métodos de acceso (getters y setters) para cada atributo del token de recuperación.
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
    
    public boolean getusado() { return usado; }
    public void setusado (boolean usado) { this.usado = usado; } 
    
    public int getintentos() { return intentos; }
    public void setintentos (int intentos) { this.intentos = intentos; }   
}