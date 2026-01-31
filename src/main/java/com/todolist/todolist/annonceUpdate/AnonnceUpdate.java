package com.todolist.todolist.annonceUpdate;

import com.todolist.todolist.dao.Annonce;
import com.todolist.todolist.dao.AnnonceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AnnonceUpdate", value = "/annonce-update")
public class AnonnceUpdate extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                AnnonceDAO annonceDAO = new AnnonceDAO();
                Annonce annonce = annonceDAO.find(id);

                request.setAttribute("annonce", annonce);

                if (annonce != null) {
                    this.getServletContext()
                            .getRequestDispatcher("/annonce-update.jsp")
                            .forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            response.sendRedirect("list-annonces");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        int id;

        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("list-annonces");
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String address = request.getParameter("address");
        String mail = request.getParameter("mail");

        Annonce annonce = new Annonce(id, title, description, address, mail, null);

        if (title == null || title.trim().isEmpty()
                || description == null || description.trim().isEmpty()
                || address == null || address.trim().isEmpty()
                || mail == null || mail.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Tous les champs sont obligatoires !");
            request.setAttribute("annonce", annonce);

            this.getServletContext().getRequestDispatcher("/annonce-update.jsp").forward(request, response);
            return;
        }

        AnnonceDAO dao = new AnnonceDAO();
        dao.update(annonce);

        response.sendRedirect("annonce-list");
    }
}
