
package com.didistore.controller.admin;

import com.didistore.dao.interfaces.auth.IUsuariosDAO;
import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.model.auth.Usuarios;
import com.didistore.model.auth.enums.EstadoUsuarios;
import com.didistore.model.auth.enums.TipoDocumentos;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class AdminUsuariosControlller {

    public static void main(String[] args) {
        
        IUsuariosDAO usuarioDAO = new UsuariosDAOImpl();
        
        Usuarios nuevoUsuario = new Usuarios();
        
        nuevoUsuario.setemail("jnuryalexandrasuarez@example.com");
        nuevoUsuario.setcontrasena("1234");
        nuevoUsuario.setnombre("Nury");
        nuevoUsuario.setapellido("Suarez");
        nuevoUsuario.setdocumento("63509369");
        nuevoUsuario.settipoDocumento(TipoDocumentos.CC);
        nuevoUsuario.setperfilId(2);
        nuevoUsuario.setestado(EstadoUsuarios.Activo);
        nuevoUsuario.setemailVerificado(true);
        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
        nuevoUsuario.setfechaCreacion(fechaActual);
        nuevoUsuario.setfechaActualizacion(fechaActual);
        nuevoUsuario.setfechaUltimoLogin(fechaActual);
        
        System.out.println("Intentado insertar usuario en MySQL... ");
        usuarioDAO.insertarUsuarios(nuevoUsuario);
        System.out.println("¡Proceso de inserción finalizado con éxito!");        
    }
}