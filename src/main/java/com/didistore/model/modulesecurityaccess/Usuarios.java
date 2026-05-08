
package com.didistore.model.modulesecurityaccess;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Usuarios {
    private int idUsuario;
    private String email;
    private String contraseña;
    private String nombre;
    private String apellido;
    private String documento;
    private String tipoDocumento;
    private int perfilId;
    private String estado;
    private int emailVerificado;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;
    private Timestamp fechaUltimoLogin;
    
    public Usuarios() {}
    
    public Usuarios (int idUsuario,
            String email,
            String contraseña,
            String nombre,
            String apellido,
            String documento,
            String tipoDocumento,
            int perfilId,
            String estado,
            int emailVerificado,
            Timestamp fechaCreacion,
            Timestamp fechaActualizacion,
            Timestamp fechaUltimoLogin) {
    
        this.idUsuario = idUsuario;
        this.email = email;
        this.contraseña = contraseña;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.tipoDocumento = tipoDocumento;
        this.perfilId = perfilId;
        this.estado = estado;
        this.emailVerificado = emailVerificado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaUltimoLogin = fechaUltimoLogin;
    }
    
    public int getidUsuario() { return idUsuario; }
    public void setidUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getemail() { return email; }
    public void setemail(String email) { this.email = email; }
    
    public String getcontraseña() { return contraseña; }
    public void setcontraseña(String contraseña) { this.contraseña = contraseña; }
    
    public String getnombre() { return nombre; }
    public void setnombre(String nombre) { this.nombre = nombre; }
    
    public String getapellido() { return apellido; }
    public void setapellido(String apellido) { this.apellido = apellido; }
    
    public String getdocumento() { return documento; }
    public void setdocumento(String documento) { this.documento = documento; }
    
    public String gettipoDocumento() { return tipoDocumento; }
    public void settipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    public int getperfilId() { return perfilId; }
    public void setperfilId(int perfilId) { this.perfilId = perfilId; }
    
    public String getestado() { return estado; }
    public void setestado(String estado) { this.estado = estado; }
    
    public int getemailVerificado() { return emailVerificado; }
    public void setemailVerificado(int emailVerificado) { this.emailVerificado = emailVerificado; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Timestamp getfechaActualizacion() { return fechaActualizacion; }
    public void setfechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    public Timestamp getfechaUltimoLogin() { return fechaUltimoLogin; }
    public void setfechaUltimoLogin(Timestamp fechaUltimoLogin) { this.fechaUltimoLogin = fechaUltimoLogin; }
}