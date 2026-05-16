
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.SesionesDAOImpl;
import com.didistore.dao.interfaces.auth.ISesionesDAO;
import com.didistore.model.auth.Sesiones;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class SesionesController {

    
    public static void main(String[] args) {
        
        ISesionesDAO sesionDAO = new SesionesDAOImpl();
        
        Sesiones nuevaSesion = new Sesiones();
        
        nuevaSesion.setusuarioId(0);
        nuevaSesion.settokenSesion("");
        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
        nuevaSesion.setfechaCreacion(fechaActual);
        nuevaSesion.setfechaExpiracion(fechaActual);
        nuevaSesion.setip("");
        nuevaSesion.setuserAgent("");
        nuevaSesion.setrevocada(0);
       
        System.out.println("Intentado registrar sesión en MySQL... ");
        sesionDAO.insertarSesiones(nuevaSesion);
        System.out.println("¡Proceso de inserción finalizado con éxito!");        
    }
}