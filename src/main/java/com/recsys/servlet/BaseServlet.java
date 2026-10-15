package com.recsys.servlet;

import com.recsys.model.User;
import com.recsys.util.Html;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Shared helpers for all page servlets. */
public abstract class BaseServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        super.service(req, resp);
    }

    protected User user(HttpServletRequest req) {
        return Html.currentUser(req);
    }

    protected void render(HttpServletRequest req, HttpServletResponse resp, String title, String active, String body)
            throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write(Html.page(req, title, active, body));
    }

    protected void serverError(HttpServletRequest req, HttpServletResponse resp, Exception e) throws IOException {
        log("Unexpected error", e);
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        render(req, resp, "Something went wrong", "",
                "<section class=\"panel narrow\"><h1>Something went wrong</h1>"
              + "<p>The request could not be completed. Check that MySQL is running and the connection details "
              + "in <code>db.properties</code> are correct, then reload this page.</p></section>");
    }

    protected static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}
