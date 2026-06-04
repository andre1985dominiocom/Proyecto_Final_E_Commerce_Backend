
package com.didistore.model.auth;

import com.didistore.model.auth.enums.TipoTelefonos;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa un teléfono asociado a un usuario, con sus atributos y métodos de acceso.
public class Telefonos {
    private int idTelefono;
    private int usuarioId;
    private TipoTelefonos tipo;
    private String numero;
    private int esVerificado;
    private Timestamp fechaAgregado;
    
    // Constructor vacío y constructor con parámetros para inicializar los atributos.
    public Telefonos() {}
    
    // Constructor con parámetros para inicializar los atributos del teléfono asociado a un usuario.
    public Telefonos (int idTelefono,
            int usuarioId,
            TipoTelefonos tipo,
            String numero,
            int esVerificado,
            Timestamp fechaAgregado) {
        
        // Inicialización de los atributos del teléfono con los valores proporcionados.
        this.idTelefono = idTelefono;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.numero = numero;
        this.esVerificado = esVerificado;
        this.fechaAgregado = fechaAgregado;
    }
    
    // Métodos de acceso (getters y setters) para cada atributo del teléfono asociado a un usuario.
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