
package com.didistore.model.modulesecurityaccess;

import com.didistore.util.TipoTelefonos;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Telefonos {
    private int idTelefono;
    private int usuarioId;
    private TipoTelefonos tipo;
    private String numero;
    private int esVerificado;
    private Timestamp fechaAgregado;
    
    public Telefonos() {}
    
    public Telefonos (int idTelefono,
            int usuarioId,
            TipoTelefonos tipo,
            String numero,
            int esVerificado,
            Timestamp fechaAgregado) {
        
        this.idTelefono = idTelefono;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.numero = numero;
        this.esVerificado = esVerificado;
        this.fechaAgregado = fechaAgregado;
    }
    
    public int getidTelefono() { return idTelefono; }
    public void setidTelefono (int idTelefono) { this.idTelefono = idTelefono; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId (int usuarioId) { this.usuarioId = usuarioId; }
    
    public TipoTelefonos gettipo() { return tipo; }
    public void settipo (TipoTelefonos tipo) { this.tipo = tipo; }
    
    public String getnumero() { return numero; }
    public void setnumero (String numero) { this.numero = numero; }
    
    public int getesVerificado() { return esVerificado; }
    public void setesVerificado (int esVerificado) { this.esVerificado = esVerificado; }
    
    public Timestamp getfechaAgregado() { return fechaAgregado; }
    public void setfechaAgregado (Timestamp fechaAgregado) { this.fechaAgregado = fechaAgregado; }   
}