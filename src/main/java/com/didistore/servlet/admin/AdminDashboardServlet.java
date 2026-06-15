
package com.didistore.servlet.admin;

import com.didistore.controller.admin.AdminDashboardController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/admin/dashboard-data")
public class AdminDashboardServlet extends HttpServlet {
    
    private final AdminDashboardController dashboardController = new AdminDashboardController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        dashboardController.obtenerMetricas(request, response);
    }
}