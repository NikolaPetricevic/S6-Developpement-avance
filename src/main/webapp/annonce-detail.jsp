<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.todolist.todolist.features.annonces.entity.Annonce" %>
<%@ page import="com.todolist.todolist.features.users.entity.User" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <title>Détails de l'annonce</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .detail-container {
            max-width: 800px;
            margin: 50px auto;
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .detail-label {
            font-weight: bold;
            color: #6c757d;
            margin-bottom: 5px;
            font-size: 0.9rem;
            text-transform: uppercase;
        }
        .detail-value {
            margin-bottom: 20px;
            font-size: 1.1rem;
        }
        .status-badge {
            font-size: 0.9rem;
            padding: 5px 15px;
        }
        .title-section {
            border-bottom: 2px solid #dee2e6;
            padding-bottom: 15px;
            margin-bottom: 30px;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="detail-container">
        <%
            Annonce annonce = (Annonce) request.getAttribute("annonce");
            User currentUser = (User) session.getAttribute("loggedUser");

            if (annonce != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm");
                String formattedDate = annonce.getDate() != null ? dateFormat.format(annonce.getDate()) : "Non disponible";

                boolean isOwner = currentUser != null && annonce.getAuthor() != null && currentUser.getId().equals(annonce.getAuthor().getId());

                String badgeClass = "bg-secondary";
                if (annonce.getStatus() != null) {
                    switch (annonce.getStatus()) {
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
                }
        %>

        <div class="title-section">
            <h1 class="mb-3"><%= annonce.getTitle() %></h1>
            <div class="d-flex align-items-center">
                <span class="badge <%= badgeClass %> status-badge me-3">
                    <%= annonce.getStatus() != null ? annonce.getStatus().name() : "N/A" %>
                </span>
                <span class="text-muted">
                    <i class="fas fa-calendar-alt me-2"></i>Publié le <%= formattedDate %>
                </span>
            </div>
        </div>

        <div class="row">
            <div class="col-12">
                <div class="detail-label">
                    <i class="fas fa-align-left me-2"></i>Description
                </div>
                <div class="detail-value">
                    <%= annonce.getDescription() %>
                </div>
            </div>

            <div class="col-md-6">
                <div class="detail-label">
                    <i class="fas fa-map-marker-alt me-2"></i>Adresse
                </div>
                <div class="detail-value">
                    <%= annonce.getAdress() %>
                </div>
            </div>

            <div class="col-md-6">
                <div class="detail-label">
                    <i class="fas fa-envelope me-2"></i>Email de contact
                </div>
                <div class="detail-value">
                    <a href="mailto:<%= annonce.getMail() %>"><%= annonce.getMail() %></a>
                </div>
            </div>

            <div class="col-md-6">
                <div class="detail-label">
                    <i class="fas fa-folder me-2"></i>Catégorie
                </div>
                <div class="detail-value">
                    <%= annonce.getCategory() != null ? annonce.getCategory().getLabel() : "Non catégorisé" %>
                </div>
            </div>

            <div class="col-md-6">
                <div class="detail-label">
                    <i class="fas fa-user me-2"></i>Auteur
                </div>
                <div class="detail-value">
                    <%= annonce.getAuthor() != null ? annonce.getAuthor().getUsername() : "Anonyme" %>
                </div>
            </div>
        </div>

        <div class="mt-4 d-flex gap-2">
            <a href="annonce-list" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left me-2"></i>Retour à la liste
            </a>

            <% if (isOwner) { %>
            <a href="annonce-update?id=<%= annonce.getId() %>" class="btn btn-primary">
                <i class="fas fa-edit me-2"></i>Modifier
            </a>
            <% } %>
        </div>

        <% } else { %>
        <div class="alert alert-warning" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i>Annonce introuvable.
        </div>
        <a href="annonce-list" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-2"></i>Retour à la liste
        </a>
        <% } %>
    </div>
</div>

</body>
</html>