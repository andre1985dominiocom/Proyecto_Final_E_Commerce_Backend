
package com.didistore.model.modulelogisticslocation;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Direcciones {
    private int id_Direccion;
    private String direccion;
    private int es_Principal;
    private String barrio;
    private String referencia;
    private int ciudad_Id;
    private int usuario_Id;
    private String estado;
    private Timestamp fecha_Creacion;
    
    public Direcciones() {}
    
    public Direcciones (int id_Direccion,
            String direccion,
            int es_Principal,
            String barrio,
            String referencia,
            int ciudad_Id,
            int usuario_Id,
            String estado,
            Timestamp fecha_Creacion) {
        
        this.id_Direccion = id_Direccion;
        this.direccion = direccion;
        this.es_Principal = es_Principal;
        this.barrio = barrio;
        this.referencia = referencia;
        this.ciudad_Id = ciudad_Id;
        this.usuario_Id = usuario_Id;
        this.estado = estado;
        this.fecha_Creacion = fecha_Creacion;
    }
    
    public int getid_Direccion() { return id_Direccion; }
    public void setid_Direccion(int id_Direccion) { this.id_Direccion = id_Direccion; }
    
    public String getdireccion() { return direccion; }
    public void setdireccion(String direccion) { this.direccion = direccion; }
    
    public int getes_Principal() { return es_Principal; }
    public void setes_Principal(int es_Principal) { this.es_Principal = es_Principal; }
    
    public String getbarrio() { return barrio; }
    public void setbarrio(String barrio) { this.barrio = barrio; }
    
    public String getreferencia() { return referencia; }
    public void setreferencia(String referencia) { this.referencia = referencia; }
    
    public int getciudad_Id() { return ciudad_Id; }
    public void setciudad_Id(int ciudad_Id) { this.ciudad_Id = ciudad_Id; }
    
    public int getusuario_Id() { return usuario_Id; }
    public void setusuario_Id(int usuario_Id) { this.usuario_Id = usuario_Id; }
    
    public String getestado() { return estado; }
    public void setestado(String estado) { this.estado = estado; }
    
    public Timestamp getfecha_Creacion() { return fecha_Creacion; }
    public void setfecha_Creacion(Timestamp fecha_Creacion) { this.fecha_Creacion = fecha_Creacion; }
}