package com.todolist.todolist.annonces.servlets;

import com.todolist.todolist.annonces.service.AnnonceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AnnonceDelete", value = "/annonce-delete")
public class AnnonceDelete extends HttpServlet {

    private AnnonceService annonceService;

    @Override
    public void init() throws ServletException {
        this.annonceService = new AnnonceService();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("annonce-list");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            annonceService.deleteAnnonce(id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("annonce-list");
    }
}
