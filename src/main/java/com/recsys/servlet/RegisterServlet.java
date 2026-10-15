package com.recsys.servlet;

import com.recsys.exception.ValidationException;
import com.recsys.service.AuthService;
import com.recsys.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private final AuthService auth = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        form(req, resp, "", "", "");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = param(req, "name");
        String email = param(req, "email");
        try {
            auth.register(name, email, req.getParameter("password"));
            Html.setFlash(req, "success", "Account created. Sign in to set your preferences.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            form(req, resp, name, email, e.getMessage());
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    private void form(HttpServletRequest req, HttpServletResponse resp, String name, String email, String error)
            throws IOException {
        String body = "<section class=\"panel narrow\"><h1>Create your account</h1>"
                + "<p class=\"muted\">Tell us what you like and your shelf fills itself.</p>"
                + (error.isEmpty() ? "" : Html.alert("error", error))
                + "<form method=\"post\" action=\"" + req.getContextPath() + "/register\">"
                + "<label for=\"name\">Name</label>"
                + "<input id=\"name\" name=\"name\" required maxlength=\"80\" value=\"" + Html.esc(name) + "\">"
                + "<label for=\"email\">Email</label>"
                + "<input id=\"email\" name=\"email\" type=\"email\" required value=\"" + Html.esc(email) + "\">"
                + "<label for=\"password\">Password</label>"
                + "<input id=\"password\" name=\"password\" type=\"password\" required minlength=\"8\">"
                + "<p class=\"small muted\">At least 8 characters.</p>"
                + "<button class=\"btn btn-primary\" type=\"submit\">Create account</button></form></section>";
        render(req, resp, "Create account", "register", body);
    }
}
