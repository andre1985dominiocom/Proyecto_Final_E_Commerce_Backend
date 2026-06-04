package com.didistore.controller.sales;

import com.didistore.model.sales.DetallesPedidos;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class CarritoController {

    public List<DetallesPedidos> convertirItemsEnDetalles(List<ItemSolicitudCarrito> items) {
        List<DetallesPedidos> detalles = new ArrayList<>();

        if (items == null) {
            return detalles;
        }

        for (ItemSolicitudCarrito item : items) {
            if (item == null || item.getCantidad() <= 0 || item.getPrecioUnitario() < 0) {
                continue;
            }

            DetallesPedidos detalle = new DetallesPedidos();
            detalle.setproductoId(item.getProductoId());
            detalle.setcantidad(item.getCantidad());
            detalle.setprecioUnitario(item.getPrecioUnitario());
            detalle.setsubtotal(item.getCantidad() * item.getPrecioUnitario());
            detalles.add(detalle);
        }

        return detalles;
    }

    public double calcularSubtotal(List<DetallesPedidos> detalles) {
        double subtotal = 0;
        if (detalles == null) {
            return subtotal;
        }

        for (DetallesPedidos detalle : detalles) {
            subtotal += detalle.getsubtotal();
        }
        return subtotal;
    }

    public int contarUnidades(List<DetallesPedidos> detalles) {
        int total = 0;
        if (detalles == null) {
            return total;
        }

        for (DetallesPedidos detalle : detalles) {
            total += detalle.getcantidad();
        }
        return total;
    }

    public static class ItemSolicitudCarrito {
        private int productoId;
        private String nombreProducto;
        private int cantidad;
        private double precioUnitario;

        public int getProductoId() {
            return productoId;
        }

        public void setProductoId(int productoId) {
            this.productoId = productoId;
        }

        public String getNombreProducto() {
            return nombreProducto;
        }

        public void setNombreProducto(String nombreProducto) {
            this.nombreProducto = nombreProducto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(double precioUnitario) {
            this.precioUnitario = precioUnitario;
        }
    }
}
