package com.todolist.todolist.servlets;

import com.todolist.todolist.entities.Annonce;
import com.todolist.todolist.entities.Category;
import com.todolist.todolist.services.AnnonceService;
import com.todolist.todolist.services.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AnnonceUpdate", value = "/annonce-update")
public class AnnonceUpdate extends HttpServlet {

    private AnnonceService annonceService;
    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        this.annonceService = new AnnonceService();
        this.categoryService = new CategoryService();
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

            List<Category> categories = categoryService.findAll();

            request.setAttribute("annonce", annonce);
            request.setAttribute("categories", categories);

            this.getServletContext()
                    .getRequestDispatcher("/annonce-update.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("annonce-list");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("annonce-list");
            return;
        }

        Long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("annonce-list");
            return;
        }

        Annonce annonce = annonceService.findOne(id);
        if (annonce == null) {
            response.sendRedirect("annonce-list");
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");
        String categoryIdParam = request.getParameter("categoryId");

        if (title == null || title.trim().isEmpty()
                || description == null || description.trim().isEmpty()
                || adress == null || adress.trim().isEmpty()
                || mail == null || mail.trim().isEmpty()
                || categoryIdParam == null || categoryIdParam.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Tous les champs sont obligatoires !");

            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);

            setAttributes(request, annonce, categoryIdParam);
            this.getServletContext().getRequestDispatcher("/annonce-update.jsp").forward(request, response);
            return;
        }

        Long categoryId;
        try {
            categoryId = Long.parseLong(categoryIdParam);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Catégorie invalide !");
            setAttributes(request, annonce, categoryIdParam);
            this.getServletContext().getRequestDispatcher("/annonce-update.jsp").forward(request, response);
            return;
        }

        Category category = categoryService.findOne(categoryId);
        if (category == null) {
            request.setAttribute("errorMessage", "Catégorie introuvable !");
            setAttributes(request, annonce, categoryIdParam);
            this.getServletContext().getRequestDispatcher("/annonce-update.jsp").forward(request, response);
            return;
        }

        annonce.setTitle(title.trim());
        annonce.setDescription(description.trim());
        annonce.setAdress(adress.trim());
        annonce.setMail(mail.trim());
        annonce.setCategory(category);

        annonceService.updateAnnonce(annonce);

        response.sendRedirect("annonce-list");
    }

    private void setAttributes(HttpServletRequest request, Annonce annonce, String categoryIdParam) {
        request.setAttribute("annonce", annonce);

        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            try {
                Long catId = Long.parseLong(categoryIdParam);
                Category tempCategory = new Category();
                tempCategory.setId(catId);
                annonce.setCategory(tempCategory);
            } catch (NumberFormatException ignored) {
            }
        }

        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories", categories);
    }
}
