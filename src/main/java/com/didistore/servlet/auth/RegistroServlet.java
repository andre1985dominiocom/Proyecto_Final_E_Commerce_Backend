
package com.didistore.servlet.auth;

import com.didistore.controller.auth.UsuariosController;
import com.didistore.model.auth.Usuarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/auth/registro")
public class RegistroServlet extends HttpServlet {
    
    private UsuariosController registroController;

    @Override
    public void init() {

        registroController = new UsuariosController();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String nombre = request.getParameter("nombre");

        String email = request.getParameter("email");

        String documento = request.getParameter("documento");

        String password = request.getParameter("password");

        Usuarios usuario = new Usuarios();

        usuario.setnombre(nombre);
        usuario.setemail(email);
        usuario.setdocumento(documento);
        usuario.setcontrasena(password);

        usuario.setperfilId(3);

        boolean creado = registroController.registrarCliente(usuario);

        PrintWriter out = response.getWriter();

        if(creado) {

            out.print(""" 
            {
              "ok" : true,
              "message" : "Cliente registrado correctamente"
            }
            """);
        } else {
            response.setStatus(400);

            out.print("""
            {
              "ok":false,
              "message":"No fue posible registrar cliente"
            }
            """);
        }
    }    
}