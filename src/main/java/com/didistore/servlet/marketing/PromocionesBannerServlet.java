package com.didistore.servlet.marketing;

import com.didistore.controller.marketing.PromocionesController;
import com.didistore.model.marketing.Promociones;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

// Banner de promociones activas para la página principal (CU-CLI-01 / RF-09).
// GET público bajo /catalog/*.
@WebServlet("/catalog/promociones")
public class PromocionesBannerServlet extends HttpServlet {

    private final PromocionesController promocionesController = new PromocionesController();
    // Gson por defecto no puede reflejar java.time.LocalDateTime en Java 17+ (módulos).
    private final Gson gson = new com.google.gson.GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, type, context) ->
                            new com.google.gson.JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonDeserializer<LocalDateTime>) (json, type, context) ->
                            LocalDateTime.parse(json.getAsString()))
            .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Promociones> activas = promocionesController.listarActivasParaBanner();
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(activas));
    }
}
