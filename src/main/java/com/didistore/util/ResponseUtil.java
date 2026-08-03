
package com.didistore.util;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ResponseUtil {
    
    public static void jsonResponse(HttpServletResponse response, String json) throws IOException {

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.getWriter().print(json);
            }

    public static void success(HttpServletResponse response, String mensaje) throws IOException {

                jsonResponse(response, "{\"success\":true,\"message\":\"" + mensaje + "\"}");
            }

    public static void error(HttpServletResponse response, int status, String mensaje) throws IOException {

                response.setStatus(status);

                jsonResponse(response, "{\"success\":false,\"message\":\"" + mensaje + "\"}");
            }
}