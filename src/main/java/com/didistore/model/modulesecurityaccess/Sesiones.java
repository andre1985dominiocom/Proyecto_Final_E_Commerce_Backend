
package com.didistore.model.modulesecurityaccess;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Sesiones {
    private int id_Sesion;
    private int usuario_Id;
    private String token_Sesion;
    private Timestamp fecha_Creacion;
    private Timestamp fecha_Expiracion;
    private String ip;
    private String user_Agent;
    private int revocada;
    
    public Sesiones() {}
    
    public Sesiones (int id_Sesion,
            int usuario_Id,
            String token_Sesion,
            Timestamp fecha_Creacion,
            Timestamp fecha_Expiracion,
            String ip,
            String user_Agent,
            int revocada) {
        
        this.id_Sesion = id_Sesion;
        this.usuario_Id = usuario_Id;
        this.token_Sesion = token_Sesion;
        this.fecha_Creacion = fecha_Creacion;
        this.fecha_Expiracion = fecha_Expiracion;
        this.ip = ip;
        this.user_Agent = user_Agent;
        this.revocada = revocada;
    }
    
    public int getid_Sesion() { return id_Sesion; }
    public void setid_Sesion (int id_Sesion) { this.id_Sesion = id_Sesion; }
    
    public int getusuario_Id() { return usuario_Id; }
    public void setusuario_Id (int usuario_Id) { this.usuario_Id = usuario_Id; }
    
    public String gettoken_Sesion() { return token_Sesion; }
    public void settoken_Sesion (String token_Sesion) { this.token_Sesion = token_Sesion; }
    
    public Timestamp getfecha_Creacion() { return fecha_Creacion; }
    public void setfecha_Creacion (Timestamp fecha_Creacion) { this.fecha_Creacion = fecha_Creacion; }
    
    public Timestamp getfecha_Expiracion() { return fecha_Expiracion; }
    public void setfecha_Expiracion (Timestamp fecha_Expiracion) { this.fecha_Expiracion = fecha_Expiracion; }
    
    public String getip() { return ip; }
    public void setip (String ip) { this.ip = ip; } 
    
    public String getuser_Agent() { return user_Agent; }
    public void setuser_Agent (String user_Agent) { this.user_Agent = user_Agent; }  
    
    public int getrevocada() { return revocada; }
    public void setrevocada (int revocada) { this.revocada = revocada; }    
}