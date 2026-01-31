package com.todolist.todolist;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        renderPage(request, response, null);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        renderPage(request, response, name);
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response, String name) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");

        out.println("<h1>" + message + "</h1>");

        out.println("<form action=\"hello-servlet\" method=\"POST\">");
        out.println("  <input type=\"text\" id=\"name\" name=\"name\" placeholder=\"Votre nom\"/>");
        out.println("  <button type=\"submit\"> Envoyer </button>");
        out.println("</form>");

        if (name != null && !name.isEmpty()) {
            out.println("<p>" + message + " " + name + " !</p>");
        }

        out.println("</body></html>");
    }

    public void destroy() {
    }
}