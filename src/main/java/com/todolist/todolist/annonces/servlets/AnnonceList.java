package com.todolist.todolist.annonces.servlets;

import com.todolist.todolist.annonces.utils.AnnonceSearchCriteria;
import com.todolist.todolist.annonces.service.AnnonceService;
import com.todolist.todolist.annonces.entity.Annonce;
import com.todolist.todolist.categories.entity.Category;
import com.todolist.todolist.annonces.enums.StatusEnum;
import com.todolist.todolist.categories.service.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AnnonceList", value = "/annonce-list")
public class AnnonceList extends HttpServlet {

    private static final int PAGE_SIZE = 5;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AnnonceService annonceService = new AnnonceService();
        CategoryService categoryService = new CategoryService();

        int currentPage = 0;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 0) currentPage = 0;
            } catch (NumberFormatException e) {
                currentPage = 0;
            }
        }

        AnnonceSearchCriteria criteria = buildSearchCriteria(request);

        List<Annonce> annonces = annonceService.findByCriteria(criteria, currentPage, PAGE_SIZE);
        long totalAnnonces = annonceService.countByCriteria(criteria);
        int totalPages = (int) Math.ceil((double) totalAnnonces / PAGE_SIZE);

        List<Category> categories = categoryService.findAll();

        request.setAttribute("annonces", annonces);
        request.setAttribute("categories", categories);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("keyword", criteria.getKeyword() != null ? criteria.getKeyword() : "");
        request.setAttribute("status", criteria.getStatus() != null ? criteria.getStatus().name() : "");
        request.setAttribute("categoryId", criteria.getCategoryId() != null ? criteria.getCategoryId().toString() : "");

        this.getServletContext()
                .getRequestDispatcher("/annonce-list.jsp")
                .forward(request, response);
    }

    private AnnonceSearchCriteria buildSearchCriteria(HttpServletRequest request) {
        AnnonceSearchCriteria.AnnonceSearchCriteriaBuilder builder = AnnonceSearchCriteria.builder();

        String keyword = request.getParameter("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            builder.keyword(keyword.trim());
        }

        String statusParam = request.getParameter("status");
        if (statusParam != null && !statusParam.trim().isEmpty()) {
            try {
                builder.status(StatusEnum.valueOf(statusParam.trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        String categoryIdParam = request.getParameter("categoryId");
        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            try {
                builder.categoryId(Long.parseLong(categoryIdParam.trim()));
            } catch (NumberFormatException ignored) {
            }
        }

        return builder.build();
    }
}