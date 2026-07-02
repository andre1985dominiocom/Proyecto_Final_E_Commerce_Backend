package com.didistore.servlet.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.didistore.controller.auth.TelefonosController;
import com.didistore.model.auth.Telefonos;
import com.didistore.model.auth.enums.TipoTelefonos;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Permitir el acceso a este servlet solo a usuarios autenticados,
// se puede implementar un filtro de autenticación que verifique
// si el usuario ha iniciado sesión antes de permitir el acceso a este servlet.
// Esto se puede hacer mediante la creación de un filtro que intercepte las solicitudes
// y verifique la sesión del usuario. Si el usuario no está autenticado,
// se puede redirigir a una página de inicio de sesión o devolver un error de autorización.
@WebServlet("/auth/telefonos")
public class TelefonosServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final TelefonosController telefonosController = new TelefonosController();

    // GET /auth/telefonos?usuarioId=1 -> Obtener el teléfono del usuario con ID 1
    // GET /auth/telefonos -> Obtener todos los teléfonos
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String usuarioIdParam = request.getParameter("usuarioId");

        if (usuarioIdParam != null) {
            try {
                int usuarioId = Integer.parseInt(usuarioIdParam);
                Telefonos telefono = telefonosController.consultarTelefonoPorUsuario(usuarioId);

                if (telefono != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(telefono));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Teléfono no encontrado\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"usuarioId inválido\"}");
            }
        } else {
            List<Telefonos> lista = telefonosController.listarTelefonos();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(lista));
        }
    }

    // POST /auth/telefonos -> Agregar un nuevo teléfono
    // El cuerpo de la solicitud debe contener un JSON con los campos usuarioId, numero y tipo
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        TelefonoRequest telefonoRequest = gson.fromJson(reader, TelefonoRequest.class);

        Map<String, Object> resultado = new HashMap<>();

        if (telefonoRequest == null
                || telefonoRequest.getUsuarioId() <= 0
                || telefonoRequest.getNumero() == null || telefonoRequest.getNumero().trim().isEmpty()
                || telefonoRequest.getTipo() == null || telefonoRequest.getTipo().trim().isEmpty()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Datos del teléfono inválidos");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        try {
            TipoTelefonos tipo = TipoTelefonos.valueOf(telefonoRequest.getTipo().toUpperCase());

            telefonosController.agregarTelefono(
                    telefonoRequest.getUsuarioId(),
                    telefonoRequest.getNumero(),
                    tipo
            );

            response.setStatus(HttpServletResponse.SC_CREATED);
            resultado.put("success", true);
            resultado.put("message", "Teléfono agregado correctamente");

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Tipo de teléfono inválido");
        }

        response.getWriter().write(gson.toJson(resultado));
    }

    // Clase interna para mapear la solicitud JSON a un objeto Java
    public static class TelefonoRequest {
        private int usuarioId;
        private String numero;
        private String tipo;

        public int getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(int usuarioId) {
            this.usuarioId = usuarioId;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }
    }
}
