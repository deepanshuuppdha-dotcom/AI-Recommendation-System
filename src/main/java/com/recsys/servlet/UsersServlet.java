package com.recsys.servlet;

import com.recsys.dao.UserDAO;
import com.recsys.exception.UserNotFoundException;
import com.recsys.model.User;
import com.recsys.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/** Admin: manage user profiles (list and remove). */
@WebServlet("/admin/users")
public class UsersServlet extends BaseServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int me = user(req).getId();
            StringBuilder sb = new StringBuilder("<section class=\"page-head\"><h1>Users</h1>")
                .append("<p class=\"muted\">Every account in the system. Removing a user also deletes their preferences and history.</p></section>")
                .append("<section class=\"panel\"><div class=\"scroll\"><table><thead><tr><th>Name</th><th>Email</th><th>Role</th><th></th></tr></thead><tbody>");
            for (User u : userDAO.findAll()) {
                sb.append("<tr><td>").append(Html.esc(u.getName())).append("</td><td>").append(Html.esc(u.getEmail()))
                  .append("</td><td>").append(u.getRole()).append("</td><td>");
                if (u.getId() != me) {
                    sb.append("<form method=\"post\" action=\"").append(req.getContextPath()).append("/admin/users\">")
                      .append("<input type=\"hidden\" name=\"id\" value=\"").append(u.getId()).append("\">")
                      .append("<button class=\"btn btn-danger\" type=\"submit\">Remove</button></form>");
                }
                sb.append("</td></tr>");
            }
            sb.append("</tbody></table></div></section>");
            render(req, resp, "Users", "users", sb.toString());
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(param(req, "id"));
            if (id == user(req).getId()) {
                Html.setFlash(req, "error", "You cannot remove your own account.");
            } else {
                User target = userDAO.findById(id).orElseThrow(() -> new UserNotFoundException("User " + id));
                userDAO.delete(id);
                Html.setFlash(req, "success", target.getName() + " was removed.");
            }
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user id.");
        } catch (UserNotFoundException e) {
            Html.setFlash(req, "error", "That user no longer exists.");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }
}
