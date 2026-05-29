
package com.didistore.controller.auth.servlet;

import com.didistore.controller.auth.UsuariosController;
import com.didistore.model.auth.Usuarios;
import com.didistore.util.PasswordUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/usuarios")
public class UsuariosServlet extends HttpServlet {
    
    private final UsuariosController usuariosController = new UsuariosController();
    private final Gson gson = new Gson();

    // Expresiones regulares para la validación interna de seguridad del Backend
    private static final String REGEX_LETRAS = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
    private static final String REGEX_DOCUMENTO = "^\\d{10,12}$";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Usuarios> lista = usuariosController.listarUsuario();
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(lista));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Usuarios nuevoUsuario = gson.fromJson(reader, Usuarios.class);
        
        Map<String, Object> resultado = new HashMap<>();

        // 1. Validar primero que el objeto no sea nulo para evitar NullPointerException
        if (nuevoUsuario == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Body inválido o vacío");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        if (nuevoUsuario.getnombre() == null || !nuevoUsuario.getnombre().matches(REGEX_LETRAS)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "El nombre solo debe contener letras y espacios.");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        if (nuevoUsuario.getapellido() == null || !nuevoUsuario.getapellido().matches(REGEX_LETRAS)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "El apellido solo debe contener letras y espacios.");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        if (nuevoUsuario.getdocumento() == null || !nuevoUsuario.getdocumento().matches(REGEX_DOCUMENTO)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "El documento debe contener estrictamente entre 10 y 12 dígitos numéricos.");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        String clavePlana = nuevoUsuario.getcontrasena(); 
        
        if (!PasswordUtil.esSegura(clavePlana)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "La contraseña no cumple con los requisitos mínimos (8 caracteres, 1 mayúscula, 1 minúscula, 1 número).");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        // Procedemos a hashear tras pasar el filtro de complejidad
        String claveHasheada = PasswordUtil.encrypt(clavePlana);
        nuevoUsuario.setcontrasena(claveHasheada);
        
        // Tiempos de auditoría
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        nuevoUsuario.setfechaCreacion(ahora);
        nuevoUsuario.setfechaActualizacion(ahora);
        
        usuariosController.insertarUsuario(nuevoUsuario);

        response.setStatus(HttpServletResponse.SC_CREATED);
        resultado.put("success", true);
        resultado.put("message", "Usuario creado correctamente");
        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Usuarios usuario = gson.fromJson(reader, Usuarios.class);

        Map<String, Object> resultado = new HashMap<>();

        if (usuario == null || usuario.getidUsuario() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Datos de usuario inválidos");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        usuariosController.actualizarUsuario(usuario);

        response.setStatus(HttpServletResponse.SC_OK);
        resultado.put("success", true);
        resultado.put("message", "Usuario actualizado correctamente");
        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> resultado = new HashMap<>();
        String id = request.getParameter("idUsuario");

        if (id == null || id.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Falta idUsuario");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        try {
            int idUsuario = Integer.parseInt(id);
            usuariosController.eliminarUsuario(idUsuario);

            response.setStatus(HttpServletResponse.SC_OK);
            resultado.put("success", true);
            resultado.put("message", "Usuario eliminado correctamente");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "idUsuario inválido");
        }

        response.getWriter().write(gson.toJson(resultado));
    }
}