
package com.didistore.controller.sales;

import com.didistore.dao.impl.catalog.InventariosDAOImpl;
import com.didistore.dao.impl.sales.CarritoComprasDAOImpl;
import com.didistore.dao.interfaces.catalog.IInventariosDAO;
import com.didistore.dao.interfaces.sales.ICarritoComprasDAO;
import com.didistore.model.sales.CarritoCompras;
import com.didistore.model.sales.ItemCarritos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Controlador para la gestión del carrito de compras, incluyendo operaciones
// como agregar productos, modificar cantidades, eliminar ítems y calcular el total del carrito.
public class CarritoController {
    
    ICarritoComprasDAO carritoDAO;
    
    public CarritoController() {
        this.carritoDAO = new CarritoComprasDAOImpl();
    }
    
    public IInventariosDAO inventariosDAO = new InventariosDAOImpl();
    
    public CarritoCompras obtenerOGenerarCarrito(Integer usuarioId, String sesionId) {
        CarritoCompras carrito = null;

        // 1. Intentar buscar por Usuario registrado (si está logueado)
        if (usuarioId != null && usuarioId != 0) {
            carrito = carritoDAO.buscarPorUsuario(usuarioId);
        } else if (sesionId != null && !sesionId.isEmpty()) {
            // 2. Si es anónimo, buscar por el ID de la sesión del navegador
            carrito = carritoDAO.buscarPorSesion(sesionId);
        }

        // 3. Si no existe un registro en la BD, aplicamos la regla de creación
        if (carrito == null) {
            carrito = new CarritoCompras();
            if (usuarioId != null && usuarioId != 0) {
                carrito.setusuarioId(usuarioId);
            } else {
                carrito.setsesionId(sesionId);
            }
            
            // Persistir el nuevo carrito vacío en la BD
            carritoDAO.crearCarrito(carrito);
            
            // Recuperar el carrito recién creado para obtener el ID_Carrito autogenerado
            if (usuarioId != null && usuarioId != 0) {
                carrito = carritoDAO.buscarPorUsuario(usuarioId);
            } else {
                carrito = TrumanPorSesion(sesionId);
            }
        }
        return carrito;
    }
    
    public boolean agregarProductoAlCarrito(int carritoId, int usuarioId, int productoId, int cantidad, double precioUnitario) {
        // Validar que la cantidad sea coherente
        if (cantidad <= 0) {
            return false;
        }
        
        boolean disponible = inventariosDAO.hayStockSuficiente(productoId, cantidad);
        
        if (!disponible) {
            System.out.println("Stock insuficiente");
            return false;
        }

        // Recuperar ítems actuales para verificar duplicados y respetar el UNIQUE KEY de la BD
        List<ItemCarritos> itemsActuales = carritoDAO.listarItems(carritoId);
        
        ItemCarritos itemExistente = itemsActuales.stream()
                .filter(item -> item.getproductoId() == productoId)
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            // Si ya existe, sumamos la cantidad a la fila existente
            int nuevaCantidad = itemExistente.getcantidad() + cantidad;
            
            boolean stockNuevo = inventariosDAO.hayStockSuficiente(productoId, nuevaCantidad);
            
            if (!stockNuevo) {
                return false;
            }
            return carritoDAO.actualizarCantidad(itemExistente.getidItem(), nuevaCantidad);
        } else {
            // Si es un producto nuevo en el carrito, creamos el registro limpio
            ItemCarritos nuevoItem = new ItemCarritos();
            nuevoItem.setcarritoId(carritoId);
            nuevoItem.setproductoId(productoId);
            nuevoItem.setcantidad(cantidad);
            nuevoItem.setprecioUnitario(precioUnitario);
            
            return carritoDAO.agregarItem(nuevoItem);
        }
    }
    
    public boolean modificarCantidadItem(int idItem, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            return carritoDAO.eliminarItem(idItem);
        }
        return carritoDAO.actualizarCantidad(idItem, nuevaCantidad);
    }
    
    public boolean eliminarItemDelCarrito(int idItem) {
        return carritoDAO.eliminarItem(idItem);
    }
    
    public boolean vaciarCarritoCompleto(int carritoId) {
        return carritoDAO.vaciarCarrito(carritoId);
    }
    
    public List<ItemCarritos> obtenerItemsDelCarrito(int carritoId) {
        return carritoDAO.listarItems(carritoId);
    }
    
    public double calcularTotalCarrito(int carritoId) {
        return carritoDAO.obtenerTotal(carritoId);
    }
    
    private CarritoCompras TrumanPorSesion(String sesionId) {
        return carritoDAO.buscarPorSesion(sesionId);
    }
}