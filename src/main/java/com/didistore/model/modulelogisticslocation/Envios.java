
package com.didistore.model.modulelogisticslocation;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Envios {
    private int id_Envio;
    private int pedido_Id;
    private int direccion_Id;
    private String transportadora;
    private String numero_Guia;
    private String estado;
    private Timestamp fecha_Despacho;
    private Timestamp fecha_Entrega_Estimada;
    private Timestamp fecha_Entrega_Real;
    private String observaciones;
    private Timestamp fecha_Creacion;
    private Timestamp fecha_Actualizacion;

    public Envios() {}

    public Envios (int id_Envio,
            int pedido_Id,
            int direccion_Id,
            String transportadora,
            String numero_Guia,
            String estado,
            Timestamp fecha_Despacho,
            Timestamp fecha_Entrega_Estimada,
            Timestamp fecha_Entrega_Real,
            String observaciones,
            Timestamp fecha_Creacion,
            Timestamp fecha_Actualizacion) {
        
        this.id_Envio = id_Envio;
        this.pedido_Id = pedido_Id;
        this.direccion_Id = direccion_Id;
        this.transportadora = transportadora;
        this.numero_Guia = numero_Guia;
        this.estado = estado;
        this.fecha_Despacho = fecha_Despacho;
        this.fecha_Entrega_Estimada = fecha_Entrega_Estimada;
        this.fecha_Entrega_Real = fecha_Entrega_Real;
        this.observaciones = observaciones;
        this.fecha_Creacion = fecha_Creacion;
        this.fecha_Actualizacion = fecha_Actualizacion;
    }
    
    public int getid_Envio() { return id_Envio; }
    public void setid_Envio(int id_Envio) { this.id_Envio = id_Envio; }
    
    public int getpedido_Id() { return pedido_Id; }
    public void setpedido_Id(int pedido_Id) { this.pedido_Id = pedido_Id; }
    
    public int getdireccion_Id() { return direccion_Id; }
    public void setdireccion_Id(int direccion_Id) { this.direccion_Id = direccion_Id; }
    
    public String gettransportadora() { return transportadora; }
    public void settransportadora(String transportadora) { this.transportadora = transportadora; }
    
    public String getnumero_Guia() { return numero_Guia; }
    public void setnumero_Guia(String numero_Guia) { this.numero_Guia = numero_Guia; }
    
    public String getestado() { return estado; }
    public void setestado(String estado) { this.estado = estado; }
    
    public Timestamp getfecha_Despacho() { return fecha_Despacho; }
    public void setfecha_Despacho(Timestamp fecha_Despacho) { this.fecha_Despacho = fecha_Despacho; }
    
    public Timestamp getfecha_Entrega_Estimada() { return fecha_Entrega_Estimada; }
    public void setfecha_Entrega_Estimada(Timestamp fecha_Entrega_Estimada) { this.fecha_Entrega_Estimada = fecha_Entrega_Estimada; }
    
    public Timestamp getfecha_Entrega_Real() { return fecha_Entrega_Real; }
    public void setfecha_Entrega_Real(Timestamp fecha_Entrega_Real) { this.fecha_Entrega_Real = fecha_Entrega_Real; }
    
    public String getobservaciones() { return observaciones; }
    public void setobservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public Timestamp getfecha_Creacion() { return fecha_Creacion; }
    public void setfecha_Creacion(Timestamp fecha_Creacion) { this.fecha_Creacion = fecha_Creacion; }
    
    public Timestamp getfecha_Actualizacion() { return fecha_Actualizacion; }
    public void setfecha_Actualizacion(Timestamp fecha_Actualizacion) { this.fecha_Actualizacion = fecha_Actualizacion; }
}
