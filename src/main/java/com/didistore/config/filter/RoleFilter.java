
package com.didistore.config.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebFilter("/*")
public class RoleFilter extends HttpFilter implements Filter {

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
                path.startsWith("/html/auth/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/assets/") ||
                path.startsWith("/catalog/");

        if (publicResource) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("rol") == null) {
            chain.doFilter(request, response);
            return;
        }

        String rol = String.valueOf(session.getAttribute("rol")).trim().toUpperCase();

        if (path.startsWith("/admin/usuarios")
                || path.startsWith("/auth/perfiles")
                || path.startsWith("/auth/permisos")
                || path.startsWith("/auth/perfil-permisos")
                || path.startsWith("/html/admin/users.html")) {

            if (!"ADMIN".equals(rol)) {
                denyAccess(request, response, contextPath);
                return;
            }
        }

        if (path.startsWith("/admin/")
                || path.startsWith("/html/admin/")) {

            if (!"ADMIN".equals(rol) && !"EMPLEADO".equals(rol)) {
                denyAccess(request, response, contextPath);
                return;
            }
        }

        if (path.startsWith("/html/account/")
                || path.startsWith("/html/cart/")
                || path.startsWith("/html/checkout/")
                || path.startsWith("/html/orders/")
                || path.startsWith("/sales/")) {

            if (!"CLIENTE".equals(rol) && !"ADMIN".equals(rol) && !"EMPLEADO".equals(rol)) {
                denyAccess(request, response, contextPath);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void denyAccess(HttpServletRequest request, HttpServletResponse response, String contextPath)
            throws IOException {

        if (isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"No tienes permisos para acceder a este recurso\"}");
        } else {
            response.sendRedirect(contextPath + "/index.html");
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        return "XMLHttpRequest".equalsIgnoreCase(requestedWith)
                || (accept != null && accept.contains("application/json"));
    }
}