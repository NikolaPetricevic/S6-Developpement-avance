<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.todolist.todolist.features.annonces.enums.StatusEnum" %>
<%@ page import="com.todolist.todolist.features.annonces.entity.Annonce" %>
<%@ page import="com.todolist.todolist.features.categories.entity.Category" %>
<%@ page import="com.todolist.todolist.features.users.entity.User" %>

<!DOCTYPE html>
<html>
<head>
    <title>Liste des annonces</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .list-group-item {
            border-left: none;
            border-right: none;
            border-radius: 0 !important;
        }
        .list-wrapper {
            border: 1px solid #ddd;
            border-radius: 4px;
            overflow: hidden;
            background: white;
        }
        .pagination {
            margin-top: 20px;
        }
        .search-wrapper {
            background: white;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 20px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body class="bg-light p-4">

<div class="container">

    <div class="search-wrapper">
        <form method="get" action="annonce-list" class="row g-3">
            <div class="col-md-4">
                <label for="keyword" class="form-label">Rechercher</label>
                <input type="text"
                       class="form-control"
                       id="keyword"
                       name="keyword"
                       placeholder="Rechercher par titre ou description..."
                       value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>">
            </div>
            <div class="col-md-3">
                <label for="status" class="form-label">Statut</label>
                <select class="form-select" id="status" name="status">
                    <option value="">Tous les statuts</option>
                    <%
                        String selectedStatus = (String) request.getAttribute("status");
                        for (StatusEnum s : StatusEnum.values()) {
                            String selected = (selectedStatus != null && selectedStatus.equals(s.name())) ? "selected" : "";
                    %>
                    <option value="<%= s.name() %>" <%= selected %>><%= s.name() %></option>
                    <%
                        }
                    %>
                </select>
            </div>
            <div class="col-md-3">
                <label for="categoryId" class="form-label">Catégorie</label>
                <select class="form-select" id="categoryId" name="categoryId">
                    <option value="">Toutes les catégories</option>
                    <%
                        List<Category> categories = (List<Category>) request.getAttribute("categories");
                        String selectedCategoryId = (String) request.getAttribute("categoryId");
                        if (categories != null) {
                            for (Category cat : categories) {
                                String selected = (selectedCategoryId != null && selectedCategoryId.equals(cat.getId().toString())) ? "selected" : "";
                    %>
                    <option value="<%= cat.getId() %>" <%= selected %>><%= cat.getLabel() %></option>
                    <%
                            }
                        }
                    %>
                </select>
            </div>
            <div class="col-md-2 d-flex align-items-end">
                <button type="submit" class="btn btn-primary w-100">
                    <i class="fas fa-search me-2"></i>Rechercher
                </button>
            </div>
            <%
                String keyword = (String) request.getAttribute("keyword");
                String status = (String) request.getAttribute("status");
                String categoryId = (String) request.getAttribute("categoryId");
                if ((keyword != null && !keyword.isEmpty()) || (status != null && !status.isEmpty()) || (categoryId != null && !categoryId.isEmpty())) {
            %>
            <div class="col-12">
                <a href="annonce-list" class="btn btn-outline-secondary btn-sm">
                    <i class="fas fa-times me-2"></i>Effacer les filtres
                </a>
            </div>
            <% } %>
        </form>
    </div>

    <div class="list-wrapper mb-3">
        <div class="list-group list-group-flush">

            <%
                List<Annonce> list = (List<Annonce>) request.getAttribute("annonces");
                Integer currentPage = (Integer) request.getAttribute("currentPage");
                Integer totalPages = (Integer) request.getAttribute("totalPages");
                User currentUser = (User) session.getAttribute("loggedUser");

                if (list != null && !list.isEmpty()) {
                    for (Annonce a : list) {
                        boolean isOwner = currentUser != null && a.getAuthor() != null && currentUser.getId().equals(a.getAuthor().getId());
            %>

            <div class="list-group-item list-group-item-action py-3" style="cursor: pointer;" onclick="window.location.href='annonce-detail?id=<%= a.getId() %>'">

                <div class="d-flex justify-content-between align-items-center">
                    <div class="text-truncate me-3 flex-grow-1">
                        <span class="fw-normal text-dark"><%= a.getTitle() %></span>
                        <span class="text-muted"> (<%= a.getMail() %>)</span>

                        <% if (a.getStatus() != null) {
                            String badgeClass = "";
                            switch (a.getStatus()) {
                                case PUBLISHED:
                                    badgeClass = "bg-success";
                                    break;
                                case DRAFT:
                                    badgeClass = "bg-warning text-dark";
                                    break;
                                case ARCHIVED:
                                    badgeClass = "bg-secondary";
                                    break;
                            }
                        %>
                        <span class="badge <%= badgeClass %> ms-2"><%= a.getStatus().name() %></span>
                        <% } %>
                    </div>

                    <div class="d-flex align-items-center text-muted gap-2" style="white-space: nowrap;">
                        <span class="small">
                            <%= a.getDate() %>
                        </span>

                        <% if (isOwner) { %>
                        <% if (a.getStatus() == StatusEnum.DRAFT) { %>
                        <a href="annonce-publish?id=<%= a.getId() %>"
                           class="btn btn-sm btn-success"
                           onclick="event.stopPropagation();"
                           title="Publier cette annonce">
                            <i class="fas fa-upload me-1"></i>Publier
                        </a>
                        <% } %>

                        <% if (a.getStatus() == StatusEnum.PUBLISHED) { %>
                        <a href="annonce-archive?id=<%= a.getId() %>"
                           class="btn btn-sm btn-secondary"
                           onclick="event.stopPropagation();"
                           title="Archiver cette annonce">
                            <i class="fas fa-archive me-1"></i>Archiver
                        </a>
                        <% } %>

                        <a href="annonce-update?id=<%= a.getId() %>"
                           class="btn btn-sm btn-outline-primary"
                           onclick="event.stopPropagation();">
                            <i class="fas fa-edit me-1"></i>Modifier
                        </a>
                        <% } %>

                        <i class="fas fa-chevron-right"></i>
                    </div>
                </div>
            </div>

            <%
                }
            } else {
            %>
            <div class="p-3 text-center text-muted">Aucune annonce trouvée.</div>
            <% } %>

        </div>
    </div>

    <%
        if (totalPages != null) {
            StringBuilder params = new StringBuilder();

            if (keyword != null && !keyword.isEmpty()) {
                params.append("&keyword=").append(keyword);
            }
            if (status != null && !status.isEmpty()) {
                params.append("&status=").append(status);
            }
            if (categoryId != null && !categoryId.isEmpty()) {
                params.append("&categoryId=").append(categoryId);
            }

            String urlParams = params.toString();
    %>
    <nav aria-label="Pagination des annonces">
        <ul class="pagination justify-content-center">
            <li class="page-item <%= (currentPage == 0) ? "disabled" : "" %>">
                <a class="page-link" href="?page=<%= currentPage - 1 %><%= urlParams %>" aria-label="Précédent">
                    <span aria-hidden="true">&laquo;</span>
                </a>
            </li>

            <%
                int startPage = Math.max(0, currentPage - 2);
                int endPage = Math.min(totalPages - 1, currentPage + 2);

                if (startPage > 0) {
            %>
            <li class="page-item">
                <a class="page-link" href="?page=0<%= urlParams %>">1</a>
            </li>
            <%
                if (startPage > 1) {
            %>
            <li class="page-item disabled">
                <span class="page-link">...</span>
            </li>
            <%
                    }
                }

                for (int i = startPage; i <= endPage; i++) {
            %>
            <li class="page-item <%= (i == currentPage) ? "active" : "" %>">
                <a class="page-link" href="?page=<%= i %><%= urlParams %>"><%= i + 1 %></a>
            </li>
            <%
                }

                if (endPage < totalPages - 1) {
                    if (endPage < totalPages - 2) {
            %>
            <li class="page-item disabled">
                <span class="page-link">...</span>
            </li>
            <%
                }
            %>
            <li class="page-item">
                <a class="page-link" href="?page=<%= totalPages - 1 %><%= urlParams %>"><%= totalPages %></a>
            </li>
            <%
                }
            %>

            <li class="page-item <%= (currentPage >= totalPages - 1) ? "disabled" : "" %>">
                <a class="page-link" href="?page=<%= currentPage + 1 %><%= urlParams %>" aria-label="Suivant">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>
        </ul>
    </nav>
    <% } %>

    <a href="annonce-add" class="btn btn-outline-secondary d-inline-flex align-items-center">
        <i class="fas fa-plus me-2"></i> Ajouter une annonce
    </a>

    <a href="logout" class="btn btn-outline-danger">
        <i class="fas fa-sign-out-alt me-2"></i>Déconnexion
    </a>
</div>

</body>
</html>