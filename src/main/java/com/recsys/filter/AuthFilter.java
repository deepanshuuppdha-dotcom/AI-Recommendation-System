package com.recsys.filter;

import com.recsys.model.User;
import com.recsys.util.Html;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Protects /admin/* (admins only) and /user/* (signed-in users only). */
@WebFilter(urlPatterns = {"/admin/*", "/user/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String ctx = req.getContextPath();

        User user = Html.currentUser(req);
        if (user == null) {
            res.sendRedirect(ctx + "/login");
            return;
        }
        String path = req.getServletPath();
        if (path.startsWith("/admin") && !user.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "This area is for administrators only.");
            return;
        }
        if (path.startsWith("/user") && user.isAdmin()) {
            res.sendRedirect(ctx + user.getHomePath());
            return;
        }
        // Stop the browser from showing cached private pages after sign-out.
        res.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }
}
