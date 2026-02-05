package com.todolist.todolist.servlets;

import com.todolist.todolist.dao.Annonce;
import com.todolist.todolist.dao.AnnonceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AnnonceList", value = "/annonce-list")
public class AnnonceList extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AnnonceDAO annonceDAO = new AnnonceDAO();
        List<Annonce> annonces = annonceDAO.findAll();

        request.setAttribute("annonces", annonces);

        this.getServletContext()
                .getRequestDispatcher("/annonce-list.jsp")
                .forward(request, response);
    }
}
