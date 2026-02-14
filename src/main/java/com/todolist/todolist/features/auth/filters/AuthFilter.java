package com.todolist.todolist.features.auth.filters;

import com.todolist.todolist.features.auth.service.AuthService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/annonce-list",
        "/annonce-add",
        "/annonce-update",
        "/annonce-detail"})
public class AuthFilter implements Filter {

    private final AuthService authService = new AuthService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (authService.isLoggedIn(httpRequest.getSession())) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect("login");
        }
    }
}