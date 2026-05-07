
package com.didistore.model.modulesecurityaccess;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Tokens_Recuperacion {
    private int id_Token;
    private int usuario_Id;
    private String token_Hash;
    private Timestamp fecha_Creacion;
    private Timestamp fecha_Expiracion;
    private int usado;
    private int intentos;
    
    public Tokens_Recuperacion() {}
    
    public Tokens_Recuperacion (int id_Token,
            int usuario_Id,
            String token_Hash,
            Timestamp fecha_Creacion,
            Timestamp fecha_Expiracion,
            int usado,
            int intentos) {
        
        this.id_Token = id_Token;
        this.usuario_Id = usuario_Id;
        this.token_Hash = token_Hash;
        this.fecha_Creacion = fecha_Creacion;
        this.fecha_Expiracion = fecha_Expiracion;
        this.usado = usado;
        this.intentos = intentos;
    }
    
    public int getid_Token() { return id_Token; }
    public void setid_Token (int id_Token) { this.id_Token = id_Token; }
    
    public int getusuario_Id() { return usuario_Id; }
    public void setusuario_Id (int usuario_Id) { this.usuario_Id = usuario_Id; }
    
    public String gettoken_Hash() { return token_Hash; }
    public void settoken_Hash (String token_Hash) { this.token_Hash = token_Hash; }
    
    public Timestamp getfecha_Creacion() { return fecha_Creacion; }
    public void setfecha_Creacion (Timestamp fecha_Creacion) { this.fecha_Creacion = fecha_Creacion; }
    
    public Timestamp getfecha_Expiracion() { return fecha_Expiracion; }
    public void setfecha_Expiracion (Timestamp fecha_Expiracion) { this.fecha_Expiracion = fecha_Expiracion; }
    
    public int getusado() { return usado; }
    public void setusado (int usado) { this.usado = usado; } 
    
    public int getintentos() { return intentos; }
    public void setintentos (int intentos) { this.intentos = intentos; }   
}