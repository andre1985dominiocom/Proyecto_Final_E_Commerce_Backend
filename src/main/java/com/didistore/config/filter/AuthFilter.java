
package com.didistore.config.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// @WebFilter("/*") indica que este filtro se aplicará a todas las solicitudes entrantes al servidor web.
// Esto significa que cada vez que un usuario intente acceder a cualquier recurso de la aplicación,
// el filtro AuthFilter se ejecutará antes de que la solicitud llegue al destino final.
@WebFilter("/*")
public class AuthFilter extends HttpFilter implements Filter {

    // El método doFilter es el núcleo del filtro. Se ejecuta para cada solicitud entrante
    // y determina si el usuario tiene acceso al recurso solicitado.
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        String path = uri.substring(contextPath.length());

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
                path.startsWith("/catalog/") ||
                path.startsWith("/auth/recuperacion") ||
                path.startsWith("/auth/validar-token") ||
                path.startsWith("/auth/restablecer-password");

        if (publicResource) {
                chain.doFilter(request, response);
                return;
            }

            HttpSession session = request.getSession(false);
            boolean authenticated = session != null && session.getAttribute("usuarioId") != null;
            
            // LOG: Esto te ayudará a ver en consola por qué te bota al login
            if (!authenticated) {
                System.out.println("DEBUG: Acceso denegado a: " + path + " - Usuario no autenticado");
            }

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