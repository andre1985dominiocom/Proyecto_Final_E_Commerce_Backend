
package com.didistore.config.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Autenticación: recursos públicos vs sesión requerida.
// Registrado en WEB-INF/web.xml (orden: AuthFilter → RoleFilter → CorsFilter).
public class AuthFilter extends HttpFilter implements Filter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        String path = uri.substring(contextPath.length());
        String method = request.getMethod();
        boolean isSafeMethod = "GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method);

        boolean publicResource =
                path.equals("/") ||
                path.equals("/index.html") ||
                path.equals("/login") ||
                path.equals("/logout") ||
                path.equals("/sales/carrito") ||
                path.startsWith("/html/auth/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/assets/") ||
                path.startsWith("/auth/recuperacion") ||
                path.startsWith("/auth/validar-token") ||
                path.startsWith("/auth/restablecer-password") ||
                path.startsWith("/catalog/cupon") ||
                path.startsWith("/catalog/promociones");

        // Envíos: cotización (opciones) pública para el carrito; rastreo/despacho requieren sesión
        boolean envioPublico = path.startsWith("/logistics/envio")
                && isSafeMethod
                && "opciones".equals(request.getParameter("accion"));

        // Catálogo: lectura pública, EXCEPTO wishlist (siempre requiere sesión).
        // Escrituras (reseñas, imágenes, wishlist) requieren sesión.
        // Cupón y promociones/banner ya son públicos (publicResource).
        if (envioPublico) {
            chain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/catalog/")) {
            boolean wishlist = path.startsWith("/catalog/wishlist");
            boolean cuponOPromos = path.startsWith("/catalog/cupon")
                    || path.startsWith("/catalog/promociones");
            if (cuponOPromos || (isSafeMethod && !wishlist)) {
                chain.doFilter(request, response);
                return;
            }
            // wishlist (cualquier método) o escrituras de catálogo → cae al check de sesión
        } else if (publicResource) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean authenticated = session != null && session.getAttribute("usuarioId") != null;

        if (!authenticated) {
            if (isAjaxRequest(request)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"Sesión no iniciada\"}");
            } else {
                response.sendRedirect(contextPath + "/html/auth/login.html");
            }
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        return "XMLHttpRequest".equalsIgnoreCase(requestedWith)
                || (accept != null && accept.contains("application/json"));
    }
}
