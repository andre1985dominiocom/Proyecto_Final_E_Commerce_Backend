
package com.didistore.model;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Usuarios {
    private int id_Usuario;
    private String email;
    private String contraseña;
    private String nombre;
    private String apellido;
    private String documento;
    private String tipo_Documento;
    private int perfil_Id;
    private String estado;
    private int email_Verificado;
    private Timestamp fecha_Creacion;
    private Timestamp fecha_Actualizacion;
    private Timestamp fecha_Ultimo_Login;
    
     public Usuarios() {}
    
    public Usuarios (int id_Usuario, 
            String email, 
            String contraseña, 
            String nombre, 
            String apellido, 
            String documento, 
            String tipo_Documento, 
            int perfil_Id,
            String estado,
            int email_Verificado, 
            Timestamp fecha_Creacion,
            Timestamp fecha_Actualizacion,
            Timestamp fecha_Ultimo_Login) {
    
        this.id_Usuario = id_Usuario;
        this.email = email;
        this.contraseña = contraseña;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.tipo_Documento = tipo_Documento;
        this.perfil_Id = perfil_Id;
        this.estado = estado;
        this.email_Verificado = email_Verificado;
        this.fecha_Creacion = fecha_Creacion;
        this.fecha_Actualizacion = fecha_Actualizacion;
        this.fecha_Ultimo_Login = fecha_Ultimo_Login;
}
    
    public int getid_Usuario() { return id_Usuario; }
    public void setid_Usuario(int id_Usuario) { this.id_Usuario = id_Usuario; }
    
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
    
    public String gettipo_Documento() { return tipo_Documento; }
    public void settipo_Documento(String tipo_Documento) { this.tipo_Documento = tipo_Documento; }
    
    public int getperfil_Id() { return perfil_Id; }
    public void setperfil_Id(int perfil_Id) { this.perfil_Id = perfil_Id; }
    
    public String getestado() { return estado; }
    public void setestado(String estado) { this.estado = estado; }
    
    public int getemail_Verificado() { return email_Verificado; }
    public void setemail_Verificado(int email_Verificado) { this.email_Verificado = email_Verificado; }
    
    public Timestamp getfecha_Creacion() { return fecha_Creacion; }
    public void setfecha_Creacion(Timestamp fecha_creacion) { this.fecha_Creacion = fecha_creacion; }
    
    public Timestamp getfecha_Actualizacion() { return fecha_Actualizacion; }
    public void setfecha_Actualizacion(Timestamp fecha_Actualizacion) { this.fecha_Actualizacion = fecha_Actualizacion; }
    
    public Timestamp getfecha_Ultimo_Login() { return fecha_Ultimo_Login; }
    public void setfecha_Ultimo_Login(Timestamp fecha_Ultimo_Login) { this.fecha_Ultimo_Login = this.fecha_Ultimo_Login; }
}