package com.todolist.todolist.annonceAdd;

import com.todolist.todolist.dao.Annonce;
import com.todolist.todolist.dao.AnnonceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AnnonceAdd", value = "/annonce-add")
public class AnnonceAdd extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.getServletContext()
                .getRequestDispatcher("/annonce-add.jsp")
                .forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String address = request.getParameter("address");
        String mail = request.getParameter("mail");


        if (title == null || title.trim().isEmpty()
                || description == null || description.trim().isEmpty()
                || address == null || address.trim().isEmpty()
                || mail == null || mail.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Tous les champs sont obligatoires !");

            request.setAttribute("title", title);
            request.setAttribute("description", description);
            request.setAttribute("address", address);
            request.setAttribute("mail", mail);

            this.getServletContext().getRequestDispatcher("/annonce-add.jsp").forward(request, response);
        }

        Annonce annonce = new Annonce(null, title, description, address, mail, null);

        AnnonceDAO dao = new AnnonceDAO();
        dao.create(annonce);

        response.sendRedirect("annonce-list");
    }

}
