package com.todolist.todolist.annonces.servlets;

import com.todolist.todolist.annonces.service.AnnonceService;
import com.todolist.todolist.annonces.entity.Annonce;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AnnonceDetail", value = "/annonce-detail")
public class AnnonceDetail extends HttpServlet {

    private AnnonceService annonceService;

    @Override
    public void init() throws ServletException {
        this.annonceService = new AnnonceService();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            request.setAttribute("annonce", annonce);

            this.getServletContext()
                    .getRequestDispatcher("/annonce-detail.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("annonce-list");
        }
    }
}