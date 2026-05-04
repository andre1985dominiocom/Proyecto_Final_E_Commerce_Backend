
package com.didistore.model;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Telefonos {
    private int id_Telefono;
    private int usuario_Id;
    private String tipo;
    private String numero;
    private int es_Verificado;
    private Timestamp fecha_Agregado;
    
    public Telefonos() {}
    
    public Telefonos (int id_Telefono,
            int usuario_Id,
            String tipo,
            String numero,
            int es_Verificado,
            Timestamp fecha_Agregado) {
        
        this.id_Telefono = id_Telefono;
        this.usuario_Id = usuario_Id;
        this.tipo = tipo;
        this.numero = numero;
        this.es_Verificado = es_Verificado;
        this.fecha_Agregado = fecha_Agregado;
    }
    
    public int getid_Telefono() { return id_Telefono; }
    public void setid_Telefono (int id_Telefono) { this.id_Telefono = id_Telefono; }
    
    public int getusuario_Id() { return usuario_Id; }
    public void setusuario_Id (int usuario_Id) { this.usuario_Id = usuario_Id; }
    
    public String gettipo() { return tipo; }
    public void settipo (String tipo) { this.tipo = tipo; }
    
    public String getnumero() { return numero; }
    public void setnumero (String numero) { this.numero = numero; }
    
    public int getes_Verificado() { return es_Verificado; }
    public void setes_Verificado (int es_Verificado) { this.es_Verificado = es_Verificado; }
    
    public Timestamp getfecha_Agregado() { return fecha_Agregado; }
    public void setfecha_Agregado (Timestamp fecha_Agregado) { this.fecha_Agregado = fecha_Agregado; }   
}