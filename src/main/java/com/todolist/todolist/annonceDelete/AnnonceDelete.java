package com.todolist.todolist.annonceDelete;

import com.todolist.todolist.dao.Annonce;
import com.todolist.todolist.dao.AnnonceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AnnonceDelete", value = "/annonce-delete")
public class AnnonceDelete extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);

                AnnonceDAO annonceDAO = new AnnonceDAO();
                Annonce annonceASupprimer = new Annonce(id, null, null, null, null, null);

                annonceDAO.delete(annonceASupprimer);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect("annonce-list");
    }

}
