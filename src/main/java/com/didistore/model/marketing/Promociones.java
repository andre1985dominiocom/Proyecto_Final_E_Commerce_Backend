
package com.didistore.model.marketing;

import com.didistore.model.marketing.enums.EstadoPromociones;
import com.didistore.model.marketing.enums.TipoDescuentos;
import java.time.LocalDateTime;
import java.sql.Timestamp;
/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Promociones {
    private int idPromocion;
    private String nombrePromocion;
    private String descripcion;
    private TipoDescuentos tipoDescuento;
    private double valorDescuento;
    private int productoId;
    private int categoriaId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoPromociones estado;
    private int aplicaConOtros;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;
    
    public Promociones() {}
    
    public Promociones(int idPromocion,
            String nombrePromocion,
            String descripcion,
            TipoDescuentos tipoDescuento,
            double valorDescuento,
            int productoId,
            int categoriaId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoPromociones estado,
            int aplicaConOtros,
            Timestamp fechaCreacion,
            Timestamp fechaActualizacion) {
        
        this.idPromocion = idPromocion;
        this.nombrePromocion = nombrePromocion;
        this.descripcion = descripcion;
        this.tipoDescuento = tipoDescuento;
        this.valorDescuento = valorDescuento;
        this.productoId = productoId;
        this.categoriaId = categoriaId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.aplicaConOtros = aplicaConOtros;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public int getidPromocion() { return idPromocion; }
    public void setidPromocion(int idPromocion) { this.idPromocion = idPromocion; }
    
    public String getnombrePromocion() { return nombrePromocion; }
    public void setnombrePromocion(String nombrePromocion) { this.nombrePromocion = nombrePromocion; }
    
    public String getdescripcion() { return descripcion; }
    public void setdescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public TipoDescuentos gettipoDescuento() { return tipoDescuento; }
    public void settipoDescuento(TipoDescuentos tipoDescuento) { this.tipoDescuento = tipoDescuento; }
    
    public double getvalorDescuento() { return valorDescuento; }
    public void setvalorDescuento(double valorDescuento) { this.valorDescuento = valorDescuento; }
    
    public int getproductoId() { return productoId; }
    public void setproductoId(int productoId) { this.productoId = productoId; }
    
    public int getcategoriaId() { return categoriaId; }
    public void setcategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
    
    public LocalDateTime getfechaInicio() { return fechaInicio; }
    public void setfechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDateTime getfechaFin() { return fechaFin; }
    public void setfechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    
    public EstadoPromociones getestado() { return estado; }
    public void setestado(EstadoPromociones estado) { this.estado = estado; }
    
    public int getaplicaConOtros() { return aplicaConOtros; }
    public void setaplicaConOtros(int aplicaConOtros) { this.aplicaConOtros = aplicaConOtros; }
    
    public Timestamp getfechaCreacion() { return fechaCreacion; }
    public void setfechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Timestamp getfechaActualizacion() { return fechaActualizacion; }
    public void setfechaActualizacion(Timestamp fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}