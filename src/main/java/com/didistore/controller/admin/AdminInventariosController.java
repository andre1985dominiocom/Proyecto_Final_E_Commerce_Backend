
package com.didistore.controller.admin;

import com.didistore.dao.impl.catalog.InventariosDAOImpl;
import com.didistore.dao.interfaces.catalog.IInventariosDAO;
import com.didistore.model.catalog.Inventarios;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class AdminInventariosController {
    
    private final IInventariosDAO inventariosDAO;
    
    public AdminInventariosController() {
        this.inventariosDAO = new InventariosDAOImpl();
    }
    
    public List<Inventarios> listarInventario() {
        return inventariosDAO.listarInventario();
    }
    
    public List<Inventarios> listarInventarioPorProducto(int productoId) {
        if (productoId <= 0) {
            return new ArrayList<>();
        }
        return inventariosDAO.listarPorProducto(productoId);
    }
    
    public Inventarios consultarInventariosPorId(int idInventario) {
        if (idInventario <= 0) {
            return null;
        }
        return inventariosDAO.consultarInventarioPorId(idInventario);
    }
    
    public boolean actualizarInventario(Inventarios inventario) {
        if (inventario == null) {
            return false;
        }

        if (inventario.getidInventario() <= 0) {
            return false;
        }

        if (inventario.getstockActual() < 0) {
            return false;
        }

        if (inventario.getstockMinimo() < 0) {
            return false;
        }

        if (inventario.getstockReservado() > inventario.getstockActual()) {
            return false;
        }

        return inventariosDAO.actualizarInventario(inventario);
    }
    
    public boolean crearInventario(Inventarios inventario) {
        
        int productoId = 0;
        int stockActual = 0;
        int stockMinimo = 0;
        int stockReservado = 0;
        
        try {
            if (productoId <= 0 || stockActual < 0 || stockMinimo < 0 || stockReservado < 0) {
                System.err.println("Datos de inventario inválidos.");
                return false;
            }
            
            Inventarios nuevoInventario = new Inventarios();
            nuevoInventario.setproductoId(productoId);
            nuevoInventario.setstockActual(stockActual);
            nuevoInventario.setstockMinimo(stockMinimo);
            nuevoInventario.setstockReservado(stockReservado);
            
            Timestamp fechaActual = new Timestamp(new Date().getTime());
            nuevoInventario.setfechaCreacion(fechaActual);
            nuevoInventario.setfechaActualizacion(fechaActual);
            
            boolean insertado = inventariosDAO.insertarInventario(nuevoInventario);
            return insertado;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }       
    }
    
    public boolean eliminarInventario(int idInventario) {
        if (idInventario <= 0) {
            return false;
        }
        return inventariosDAO.eliminarInventario(idInventario);
    }
}