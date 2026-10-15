package com.recsys.servlet;

import com.recsys.exception.AuthenticationException;
import com.recsys.exception.UserNotFoundException;
import com.recsys.model.User;
import com.recsys.service.AuthService;
import com.recsys.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private final AuthService auth = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = user(req);
        if (u != null) {
            resp.sendRedirect(req.getContextPath() + u.getHomePath());
            return;
        }
        form(req, resp, "", "");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = param(req, "email");
        String password = req.getParameter("password");
        try {
            User u = auth.login(email, password);
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();              // new session id after login
            req.getSession(true).setAttribute("user", u);
            resp.sendRedirect(req.getContextPath() + u.getHomePath());
        } catch (UserNotFoundException | AuthenticationException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            form(req, resp, email, "Email or password is incorrect. Check both and try again.");
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    private void form(HttpServletRequest req, HttpServletResponse resp, String email, String error) throws IOException {
        String body = "<section class=\"panel narrow\"><h1>Sign in</h1>"
                + "<p class=\"muted\">See what Shelfwise picked for you today.</p>"
                + (error.isEmpty() ? "" : Html.alert("error", error))
                + "<form method=\"post\" action=\"" + req.getContextPath() + "/login\">"
                + "<label for=\"email\">Email</label>"
                + "<input id=\"email\" name=\"email\" type=\"email\" required autofocus value=\"" + Html.esc(email) + "\">"
                + "<label for=\"password\">Password</label>"
                + "<input id=\"password\" name=\"password\" type=\"password\" required>"
                + "<button class=\"btn btn-primary\" type=\"submit\">Sign in</button></form>"
                + "<p class=\"small muted\">New here? <a href=\"" + req.getContextPath() + "/register\">Create an account</a></p>"
                + "</section>";
        render(req, resp, "Sign in", "login", body);
    }
}
