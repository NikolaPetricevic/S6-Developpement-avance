<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Connexion</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-card {
            max-width: 400px;
            width: 100%;
        }
    </style>
</head>
<body class="bg-light">

<div class="login-card">
    <div class="card shadow">
        <div class="card-body p-4">
            <h3 class="text-center mb-4">
                <i class="fas fa-user-circle me-2"></i>Connexion
            </h3>

            <%
                String error = (String) request.getAttribute("error");
                if (error != null) {
            %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i><%= error %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>

            <form action="login" method="post">
                <div class="mb-3">
                    <label for="username" class="form-label">
                        <i class="fas fa-user me-1"></i>Nom d'utilisateur
                    </label>
                    <input type="text"
                           class="form-control"
                           id="username"
                           name="username"
                           required
                           autofocus>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">
                        <i class="fas fa-lock me-1"></i>Mot de passe
                    </label>
                    <input type="password"
                           class="form-control"
                           id="password"
                           name="password"
                           required>
                </div>

                <div class="d-grid">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-sign-in-alt me-2"></i>Se connecter
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>