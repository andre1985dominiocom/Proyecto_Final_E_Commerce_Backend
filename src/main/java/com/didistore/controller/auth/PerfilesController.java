
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.PerfilesDAOImpl;
import com.didistore.dao.interfaces.auth.IPerfilesDAO;
import com.didistore.model.auth.Perfiles;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PerfilesController {

    public static void main(String[] args) {
        
        IPerfilesDAO perfilDAO = new PerfilesDAOImpl();
        
        Perfiles nuevoPerfil = new Perfiles();
        
        nuevoPerfil.setnombrePerfil("Cliente");
        nuevoPerfil.setdescripcionPerfil("Todas aquellas mujeres y hombres que estan interesados"
                                         + " en la comodidad para dormir mejor");
        
        System.out.println("Intentado registrar perfiles en MySQL... ");
        perfilDAO.insertarPerfiles(nuevoPerfil);                
        System.out.println("¡Proceso de inserción finalizado con éxito!");
    }   
}                              