
package com.didistore.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class SessionUtil {
    
    public static Integer obtenerUsuarioId(HttpServletRequest request) {

            HttpSession session = request.getSession(false);

            if (session == null) {
            return null;
            }

            return (Integer) session.getAttribute("usuarioId");
        }

    public static boolean usuarioAutenticado(HttpServletRequest request) {

            return obtenerUsuarioId(request) != null;
    } 
}