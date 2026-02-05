<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.todolist.todolist.dao.Annonce" %>

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
    </style>
</head>
<body class="bg-light p-4">

<div class="container">

    <div class="list-wrapper mb-3">
        <div class="list-group list-group-flush">

            <%
                List<Annonce> list = (List<Annonce>) request.getAttribute("annonces");

                if (list != null) {
                    for (Annonce a : list) {
            %>

            <a  href="annonce-update?id=<%= a.getId() %>" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center py-3">

                <div class="text-truncate me-3">
                    <span class="fw-normal"><%= a.getTitle() %></span>
                    <span class="text-muted"> (<%= a.getMail() %>)</span>
                </div>


                <div class="d-flex align-items-center text-muted" style="white-space: nowrap;">
                        <span class="small me-3">
                            <%= a.getDate() %>
                        </span>
                    <i class="fas fa-chevron-right"></i>
                </div>
            </a>

            <%
                }
            } else {
            %>
            <div class="p-3 text-center text-muted">Aucune annonce trouvée.</div>
            <% } %>

        </div>
    </div>

    <a href="annonce-add" class="btn btn-outline-secondary d-inline-flex align-items-center">
        <i class="fas fa-plus me-2"></i> Add an item
    </a>

    <a href="logout" class="btn btn-outline-danger">
        <i class="fas fa-sign-out-alt me-2"></i>Déconnexion
    </a>
</div>

</body>
</html>