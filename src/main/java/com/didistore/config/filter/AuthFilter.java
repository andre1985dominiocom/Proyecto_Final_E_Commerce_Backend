
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

@WebFilter("/*")
public class AuthFilter extends HttpFilter implements Filter {

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
                path.equals("/sales/carrito") ||
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