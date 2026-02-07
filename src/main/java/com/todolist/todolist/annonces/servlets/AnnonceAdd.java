package com.todolist.todolist.annonces.servlets;

import com.todolist.todolist.annonces.service.AnnonceService;
import com.todolist.todolist.annonces.entity.Annonce;
import com.todolist.todolist.categories.entity.Category;
import com.todolist.todolist.users.entity.User;
import com.todolist.todolist.annonces.enums.StatusEnum;
import com.todolist.todolist.auth.service.AuthService;
import com.todolist.todolist.categories.service.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@WebServlet(name = "AnnonceAdd", value = "/annonce-add")
public class AnnonceAdd extends HttpServlet {

    private AnnonceService annonceService;
    private CategoryService categoryService;
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.annonceService = new AnnonceService();
        this.categoryService = new CategoryService();
        this.authService = new AuthService();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories", categories);

        this.getServletContext()
                .getRequestDispatcher("/annonce-add.jsp")
                .forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            setAttributes(request, response, title, description, adress, mail, categoryIdParam);

            this.getServletContext().getRequestDispatcher("/annonce-add.jsp").forward(request, response);
            return;
        }

        Long categoryId;
        try {
            categoryId = Long.parseLong(categoryIdParam);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Catégorie invalide !");
            setAttributes(request, response, title, description, adress, mail, categoryIdParam);
            this.getServletContext().getRequestDispatcher("/annonce-add.jsp").forward(request, response);
            return;
        }

        User currentUser = authService.getCurrentUser(request.getSession());
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        Category category = categoryService.findOne(categoryId);
        if (category == null) {
            request.setAttribute("errorMessage", "Catégorie introuvable !");
            setAttributes(request, response, title, description, adress, mail, categoryIdParam);
            this.getServletContext().getRequestDispatcher("/annonce-add.jsp").forward(request, response);
            return;
        }

        Annonce annonce = Annonce.builder()
                .title(title.trim())
                .description(description.trim())
                .adress(adress.trim())
                .mail(mail.trim())
                .date(new Timestamp(System.currentTimeMillis()))
                .status(StatusEnum.DRAFT)
                .author(currentUser)
                .category(category)
                .build();

        annonceService.createAnnonce(annonce);

        response.sendRedirect("annonce-list");
    }

    private void setAttributes(HttpServletRequest request,
                               HttpServletResponse response,
                               String title,
                               String description,
                               String adress,
                               String mail,
                               String categoryIdParam) throws ServletException, IOException {

        request.setAttribute("title", title);
        request.setAttribute("description", description);
        request.setAttribute("adress", adress);
        request.setAttribute("mail", mail);
        request.setAttribute("categoryId", categoryIdParam);

        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories", categories);
    }
}
