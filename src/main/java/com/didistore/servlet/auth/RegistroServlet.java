
package com.didistore.servlet.auth;

import com.didistore.controller.auth.UsuariosController;
import com.didistore.model.auth.Usuarios;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/auth/registro")
public class RegistroServlet extends HttpServlet {
    
    private UsuariosController registroController;
    private final Gson gson = new Gson();

    @Override
    public void init() {
        registroController = new UsuariosController();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> resultado = new HashMap<>();
        RegistroRequest datos;

        try {
            BufferedReader reader = request.getReader();
            datos = gson.fromJson(reader, RegistroRequest.class);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("ok", false);
            resultado.put("message", "Cuerpo JSON inválido");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        if (datos == null || datos.getNombre() == null || datos.getEmail() == null
                || datos.getContrasena() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("ok", false);
            resultado.put("message", "Campos obligatorios faltantes: nombre, email, contrasena");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        Usuarios usuario = new Usuarios();
        usuario.setnombre(datos.getNombre());
        usuario.setapellido(datos.getApellido() != null ? datos.getApellido() : "");
        usuario.setemail(datos.getEmail().trim().toLowerCase());
        usuario.setdocumento(datos.getDocumento());
        usuario.setcontrasena(datos.getContrasena());
        usuario.setperfilId(3);

        boolean creado = registroController.registrarCliente(usuario);

        PrintWriter out = response.getWriter();

        if (creado) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            resultado.put("ok", true);
            resultado.put("message", "Cliente registrado correctamente");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("ok", false);
            resultado.put("message", "No fue posible registrar el cliente. Es posible que el email o documento ya existan.");
        }
        out.write(gson.toJson(resultado));
    }

    public static class RegistroRequest {
        private String nombre;
        private String apellido;
        private String tipoDocumento;
        private String documento;
        private String email;
        private String contrasena;
        private int perfilId;
        private String estado;

        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getTipoDocumento() { return tipoDocumento; }
        public String getDocumento() { return documento; }
        public String getEmail() { return email; }
        public String getContrasena() { return contrasena; }
        public int getPerfilId() { return perfilId; }
        public String getEstado() { return estado; }
    }
}