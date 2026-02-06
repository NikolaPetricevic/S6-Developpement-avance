<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.todolist.todolist.entities.Category" %>
<html>
<head>
    <title>Ajouter une annonce</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .form-container {
            max-width: 700px;
            margin: 50px auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        label {
            font-weight: bold;
            margin-bottom: 5px;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="form-container">
        <h2 class="mb-4">Ajouter une annonce</h2>

        <%
            String error = (String) request.getAttribute("errorMessage");
            if (error != null) {
        %>
        <div class="alert alert-danger" role="alert">
            <%= error %>
        </div>
        <% } %>

        <form action="annonce-add" method="POST">

            <div class="mb-3">
                <label for="title" class="form-label">Titre <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="title" name="title"
                       placeholder="Entrez le titre"
                       value="<%= request.getAttribute("title") != null ? request.getAttribute("title") : "" %>">
            </div>

            <div class="mb-3">
                <label for="description" class="form-label">Description <span class="text-danger">*</span></label>
                <textarea class="form-control" id="description" name="description" rows="4"
                          placeholder="Entrez la description"><%= request.getAttribute("description") != null ? request.getAttribute("description") : "" %></textarea>
            </div>

            <div class="mb-3">
                <label for="adress" class="form-label">Adresse <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="adress" name="adress"
                       placeholder="Entrez l'adresse"
                       value="<%= request.getAttribute("adress") != null ? request.getAttribute("adress") : "" %>">
            </div>

            <div class="mb-3">
                <label for="mail" class="form-label">Email <span class="text-danger">*</span></label>
                <input type="email" class="form-control" id="mail" name="mail"
                       placeholder="Entrez l'email"
                       value="<%= request.getAttribute("mail") != null ? request.getAttribute("mail") : "" %>">
            </div>

            <div class="mb-3">
                <label for="categoryId" class="form-label">Catégorie <span class="text-danger">*</span></label>
                <select class="form-select" id="categoryId" name="categoryId">
                    <option value="">Sélectionnez une catégorie</option>
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

            <button type="submit" class="btn btn-primary px-4">Enregistrer</button>
            <a href="annonce-list" class="btn btn-outline-secondary">
                Retour à la liste
            </a>
        </form>
    </div>
</div>

</body>
</html>