
package com.didistore.servlet.auth;

import com.didistore.controller.auth.UsuariosController;
import com.didistore.model.auth.Usuarios;
import com.didistore.util.AuditLog;
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

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Permitir el acceso a este servlet solo a usuarios con perfil de administrador
@WebServlet("/admin/usuarios")
public class UsuariosServlet extends HttpServlet {
    
    private final UsuariosController usuariosController = new UsuariosController();
    private final Gson gson = new Gson();

    // Expresiones regulares para la validación interna de seguridad del Backend
    private static final String REGEX_LETRAS = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
    private static final String REGEX_DOCUMENTO = "^\\d{10,12}$";

    // Métodos HTTP para manejar las solicitudes de usuarios
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Usuarios> lista = usuariosController.listarUsuario();
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(lista));
    }

    // Manejo de la creación de un nuevo usuario
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

        AuditLog.registrar(obtenerUsuarioId(request), obtenerEmail(request),
                "CREATE", "auth", "usuarios", null, null,
                gson.toJson(new HashMap<String, Object>() {{
                    put("email", nuevoUsuario.getemail());
                    put("perfilId", nuevoUsuario.getperfilId());
                }}), request.getRemoteAddr(), "EXITO");

        response.setStatus(HttpServletResponse.SC_CREATED);
        resultado.put("success", true);
        resultado.put("message", "Usuario creado correctamente");
        response.getWriter().write(gson.toJson(resultado));
    }

    // Manejo de la actualización de un usuario existente
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

        Usuarios usuarioBD = usuariosController.consultarUsuarioPorId(usuario.getidUsuario());

        if (usuarioBD == null) {
            resultado.put("success", false);
            resultado.put("message", "Usuario no existe");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }
        
        usuarioBD.setnombre(usuario.getnombre());
        usuarioBD.setapellido(usuario.getapellido());
        usuarioBD.setemail(usuario.getemail());
        usuarioBD.setdocumento(usuario.getdocumento());
        usuarioBD.setperfilId(usuario.getperfilId());
        usuarioBD.setestado(usuario.getestado());
        
        String password = usuario.getcontrasena();
        
        if (password != null && !password.trim().isEmpty()) {
            
            if (!password.startsWith("$2a$") &&
                !password.startsWith("$2b$") &&
                !password.startsWith("$2y$")) {
                
                usuarioBD.setcontrasena(PasswordUtil.encrypt(password));
            } else {
                usuarioBD.setcontrasena(password);
            }
        }

        usuariosController.actualizarUsuario(usuarioBD);

        AuditLog.registrar(obtenerUsuarioId(request), obtenerEmail(request),
                "UPDATE", "auth", "usuarios", usuario.getidUsuario(),
                gson.toJson(new HashMap<String, Object>() {{
                    put("estado", usuarioBD.getestado() != null ? usuarioBD.getestado().name() : null);
                }}),
                gson.toJson(new HashMap<String, Object>() {{
                    put("estado", usuario.getestado() != null ? usuario.getestado().name() : null);
                    put("perfilId", usuario.getperfilId());
                }}), request.getRemoteAddr(), "EXITO");

        response.setStatus(HttpServletResponse.SC_OK);
        resultado.put("success", true);
        resultado.put("message", "Usuario actualizado correctamente");
        response.getWriter().write(gson.toJson(resultado));
    }

    // RF-13: eliminar = anonimizar (derecho al olvido), conserva historial transaccional.
    // FA-2: requiere motivo en query param para auditoría.
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> resultado = new HashMap<>();
        String id = request.getParameter("idUsuario");
        String motivo = request.getParameter("motivo");

        if (id == null || id.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Falta idUsuario");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        if (motivo == null || motivo.trim().length() < 5) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Debe indicar un motivo de eliminación (mínimo 5 caracteres)");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        try {
            int idUsuario = Integer.parseInt(id);
            Usuarios antes = usuariosController.consultarUsuarioPorId(idUsuario);

            if (antes == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resultado.put("success", false);
                resultado.put("message", "Usuario no encontrado");
                response.getWriter().write(gson.toJson(resultado));
                return;
            }

            Map<String, Object> res = usuariosController.anonimizarUsuario(idUsuario);

            AuditLog.registrar(obtenerUsuarioId(request), obtenerEmail(request),
                    "DELETE", "auth", "usuarios", idUsuario,
                    antes != null ? gson.toJson(new HashMap<String, Object>() {{
                        put("email", antes.getemail());
                        put("nombre", antes.getnombre());
                    }}) : null,
                    gson.toJson(new HashMap<String, Object>() {{
                        put("anonimizado", true);
                        put("motivo", motivo.trim());
                    }}), request.getRemoteAddr(),
                    Boolean.TRUE.equals(res.get("success")) ? "EXITO" : "FALLIDO");

            if (Boolean.TRUE.equals(res.get("success"))) {
                response.setStatus(HttpServletResponse.SC_OK);
                resultado.put("success", true);
                resultado.put("message", res.get("message"));
                resultado.put("idUsuario", idUsuario);
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                resultado.put("success", false);
                resultado.put("message", res.get("message"));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "idUsuario inválido");
        }

        response.getWriter().write(gson.toJson(resultado));
    }

    private Integer obtenerUsuarioId(HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        return session != null ? (Integer) session.getAttribute("usuarioId") : null;
    }

    private String obtenerEmail(HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        Object email = session != null ? session.getAttribute("email") : null;
        return email != null ? email.toString() : null;
    }
}