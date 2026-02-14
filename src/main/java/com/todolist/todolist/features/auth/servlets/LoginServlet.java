package com.todolist.todolist.features.auth.servlets;

import com.todolist.todolist.features.users.entity.User;
import com.todolist.todolist.features.auth.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = authService.login(username, password, request.getSession());

        if (user != null) {
            response.sendRedirect("annonce-list");
        } else {
            request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (authService.isLoggedIn(request.getSession())) {
            response.sendRedirect("annonce-list");
        } else {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}
