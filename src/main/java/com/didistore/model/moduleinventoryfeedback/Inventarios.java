
package com.didistore.model.moduleinventoryfeedback;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Inventarios {
    private int id_Inventario;
    private int producto_Id;
    private int stock_Actual;
    private int stock_Minimo;
    private int stock_Reservado;
    private Timestamp fecha_Creacion;
    private Timestamp fecha_Actualizacion;
    
    public Inventarios() {}
    
    public Inventarios (int id_Inventario,
            int producto_Id,
            int stock_Actual,
            int stock_Mimimo,
            int stock_Reservado,
            Timestamp fecha_Creacion,
            Timestamp fecha_Actualizacion) {
        
        this.id_Inventario = id_Inventario;
        this.producto_Id = producto_Id;
        this.stock_Actual = stock_Actual;
        this.stock_Minimo = stock_Mimimo;
        this.stock_Reservado = stock_Reservado;
        this.fecha_Creacion = fecha_Creacion;
        this.fecha_Actualizacion = fecha_Actualizacion;
    }
    
    public int getid_Inventario() { return id_Inventario; }
    public void setid_Inventario(int id_Inventario) { this.id_Inventario = id_Inventario; }
    
    public int getproducto_Id() { return producto_Id; }
    public void setproducto_Id(int producto_Id) { this.producto_Id = producto_Id; }
    
    public int getstock_Actual() { return stock_Actual; }
    public void setstock_Actual(int stock_Actual) { this.stock_Actual = stock_Actual; }
    
    public int getstock_Minimo() { return stock_Minimo; }
    public void setstock_Minimo(int stock_Minimo) { this.stock_Minimo = stock_Minimo; }
    
    public int getstock_Reservado() { return stock_Reservado; }
    public void setstock_Reservado(int stock_Reservado) { this.stock_Reservado = stock_Reservado; }
    
    public Timestamp getfecha_Creacion() { return fecha_Creacion; }
    public void setfecha_Creacion(Timestamp fecha_Creacion) { this.fecha_Creacion = fecha_Creacion; }
    
    public Timestamp getfecha_Actualizacion() { return fecha_Actualizacion; }
    public void setfecha_Actualizacion(Timestamp fecha_Actualizacion) { this.fecha_Actualizacion = fecha_Actualizacion; }
}