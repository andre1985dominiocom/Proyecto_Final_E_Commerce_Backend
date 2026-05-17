
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.TelefonosDAOImpl;
import com.didistore.dao.interfaces.auth.ITelefonosDAO;
import com.didistore.model.auth.Telefonos;
import com.didistore.model.auth.enums.TipoTelefonos;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class TelefonosController {

    public static void main(String[] args) {
       
        ITelefonosDAO telefonoDAO = new TelefonosDAOImpl();
        
        Telefonos nuevoTelefono = new Telefonos();
        
        nuevoTelefono.setusuarioId(0);
        nuevoTelefono.settipo(TipoTelefonos.Principal);
        nuevoTelefono.setnumero("");
        nuevoTelefono.setesVerificado(0);
        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
        nuevoTelefono.setfechaAgregado(fechaActual);
        
        System.out.println("Intentado registrar token en MySQL... ");
        telefonoDAO.insertarTelefono(nuevoTelefono);
        System.out.println("¡Proceso de inserción finalizado con éxito!");        
    }   
}