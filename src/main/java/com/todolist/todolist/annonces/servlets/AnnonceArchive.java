package com.todolist.todolist.annonces.servlets;

import com.todolist.todolist.annonces.entity.Annonce;
import com.todolist.todolist.annonces.enums.StatusEnum;
import com.todolist.todolist.annonces.service.AnnonceService;
import com.todolist.todolist.auth.service.AuthService;
import com.todolist.todolist.users.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AnnonceArchive", value = "/annonce-archive")
public class AnnonceArchive extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("annonce-list");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Annonce annonce = annonceService.findOne(id);

            if (annonce == null) {
                response.sendRedirect("annonce-list");
                return;
            }

            User currentUser = authService.getCurrentUser(request.getSession());
            if (currentUser == null || annonce.getAuthor() == null || !currentUser.getId().equals(annonce.getAuthor().getId())) {
                response.sendRedirect("annonce-list");
                return;
            }

            if (annonce.getStatus() != StatusEnum.PUBLISHED) {
                response.sendRedirect("annonce-list");
                return;
            }

            annonceService.archive(annonce);
            response.sendRedirect("annonce-list");

        } catch (NumberFormatException e) {
            response.sendRedirect("annonce-list");
        }
    }
}
