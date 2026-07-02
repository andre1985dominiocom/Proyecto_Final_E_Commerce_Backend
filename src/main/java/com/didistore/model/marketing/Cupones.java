
 
package com.didistore.model.marketing;

import com.didistore.model.marketing.enums.TipoDescuentos;
import com.didistore.model.marketing.enums.EstadoCupones;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa un cupón de descuento en el sistema de marketing.
public class Cupones {
    private int idCupon;
    private String codigo;
    private TipoDescuentos tipoDescuento;
    private double valorDescuento;
    private double montoMinimo;
    private int cantidadMaximaUsos;
    private int cantidadUsosActuales;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoCupones estado;
    private Timestamp fechaCreacion;
    
    // Constructor vacío para la clase Cupones.
    public Cupones() {}

    // Constructor que inicializa todos los atributos de la clase Cupones.
    public Cupones(int idCupon,
            String codigo,
            TipoDescuentos tipoDescuento,
            double valorDescuento,
            double montoMinimo,
            int cantidadMaximaUsos,
            int cantidadUsosActuales,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoCupones estado,
            Timestamp fechaCreacion) {
        
        this.idCupon = idCupon;
        this.codigo = codigo;
        this.tipoDescuento = tipoDescuento;
        this.valorDescuento = valorDescuento;
        this.montoMinimo = montoMinimo;
        this.cantidadMaximaUsos = cantidadMaximaUsos;
        this.cantidadUsosActuales = cantidadUsosActuales;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }
    
    // Getters y Setters para los atributos de la clase Cupones.
    public int getidCupon() { return idCupon; }
    public void setidCupon(int idCupon) { this.idCupon = idCupon; }
    
    public String getcodigo() { return codigo; }
    public void setcodigo(String codigo) { this.codigo = codigo; }
    
    public TipoDescuentos gettipoDescuento() { return tipoDescuento; }
    public void settipoDescuento(TipoDescuentos tipoDescuento) { this.tipoDescuento = tipoDescuento; }
    
    public double getvalorDescuento() { return valorDescuento; }
    public void setvalorDescuento(double valorDescuento) { this.valorDescuento = valorDescuento; }
    
    public double getmontoMinimo() { return montoMinimo; }
    public void setmontoMinimo(double montoMinimo) { this.montoMinimo = montoMinimo; }
    
    public int getcantidadMaximaUsos() { return cantidadMaximaUsos; }
    public void setcantidadMaximaUsos(int cantidadMaximaUsos) { this.cantidadMaximaUsos = cantidadMaximaUsos; }
    
    public int getcantidadUsosActuales() { return cantidadUsosActuales; }
    public void setcantidadUsosActuales(int cantidadUsosActuales) { this.cantidadUsosActuales = cantidadUsosActuales; }
    
    public LocalDateTime getfechaInicio() { return fechaInicio; }
    public void setfechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDateTime getfechaFin() { return fechaFin; }
    public void setfechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    
    public EstadoCupones getestado() { return estado; }
    public void setestado(EstadoCupones estado) { this.estado = estado; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}