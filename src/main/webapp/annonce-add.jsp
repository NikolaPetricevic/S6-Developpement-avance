<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Formulaire de Test</title>
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
                <label for="title" class="form-label">Title</label>
                <input type="text" class="form-control" id="title" name="title"
                       placeholder="Enter title">
            </div>

            <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" name="description" rows="3" placeholder="Description"><%= request.getAttribute("description") != null ? request.getAttribute("description") : "" %></textarea>
            </div>

            <div class="mb-3">
                <label for="address" class="form-label">Address</label>
                <input type="text" class="form-control" id="address" name="address"
                       placeholder="Enter address">
            </div>

            <div class="mb-3">
                <label for="mail" class="form-label">Mail</label>
                <input type="email" class="form-control" id="mail" name="mail"
                       placeholder="Enter mail">
            </div>

            <button type="submit" class="btn btn-primary px-4">Save</button>
        </form>
    </div>
</div>

</body>
</html>