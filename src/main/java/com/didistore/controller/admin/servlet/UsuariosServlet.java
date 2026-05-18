
package com.didistore.controller.admin.servlet;

import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.model.auth.Usuarios;
import com.didistore.model.auth.enums.EstadoUsuarios;
import com.didistore.model.auth.enums.TipoDocumentos;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/usuarios")
public class UsuariosServlet extends HttpServlet {
    
    private final UsuariosDAOImpl usuariosDAO = new UsuariosDAOImpl();
    private final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        List<Usuarios> listaUsuarios = usuariosDAO.listarUsuarios();
        
        String json = gson.toJson(listaUsuarios);
        response.getWriter().write(json);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Usuarios usuario = gson.fromJson(reader, Usuarios.class);
        
        System.out.println("=== Usuario recibido en doPost ===");
        System.out.println("Nombre: " + usuario.getnombre());
        System.out.println("Apellido: " + usuario.getapellido());
        System.out.println("Email: " + usuario.getemail());
        System.out.println("Contrasena: " + usuario.getcontrasena());
        System.out.println("Documento: " + usuario.getdocumento());
        System.out.println("TipoDocumento: " + usuario.gettipoDocumento());
        System.out.println("PerfilId: " + usuario.getperfilId());
        System.out.println("Estado: " + usuario.getestado());
        System.out.println("EmailVerificado: " + usuario.getemailVerificado());

        java.sql.Timestamp ahora = new java.sql.Timestamp(System.currentTimeMillis());
        usuario.setfechaCreacion(ahora);
        usuario.setfechaActualizacion(ahora);
        usuario.setfechaUltimoLogin(ahora);

        usuariosDAO.insertarUsuarios(usuario);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("{\"message\":\"Usuario creado correctamente\"}");
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Usuarios usuario = gson.fromJson(reader, Usuarios.class);

        usuariosDAO.actualizarUsuarios(usuario);
        
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\":\"Usuario actualizado correctamente\"}");
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String id = request.getParameter("idUsuario");

        if (id != null) {
            usuariosDAO.eliminarUsuarios(Integer.parseInt(id));
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Usuario eliminado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Falta idUsuario\"}");
        }
    }
}