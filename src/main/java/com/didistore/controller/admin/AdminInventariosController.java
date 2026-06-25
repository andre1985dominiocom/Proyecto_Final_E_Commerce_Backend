
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
        
        if (inventario == null) {
            System.err.println("Datos de inventario inválidos: objeto nulo.");
            return false;
        }
        
        try {
            if (inventario.getproductoId() <= 0 || inventario.getstockActual() < 0
                    || inventario.getstockMinimo() < 0 || inventario.getstockReservado() < 0
                    || inventario.getstockReservado() > inventario.getstockActual()) {
                System.err.println("Datos de inventario inválidos: valores fuera de rango.");
                return false;
            }
            
            Timestamp fechaActual = new Timestamp(new Date().getTime());
            inventario.setfechaCreacion(fechaActual);
            inventario.setfechaActualizacion(fechaActual);
            
            return inventariosDAO.insertarInventario(inventario);
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