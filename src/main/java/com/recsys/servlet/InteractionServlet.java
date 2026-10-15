package com.recsys.servlet;

import com.recsys.dao.InteractionDAO;
import com.recsys.dao.ItemDAO;
import com.recsys.model.InteractionType;
import com.recsys.model.Item;
import com.recsys.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/** Logs a click, like or purchase, then returns the user to their recommendations. */
@WebServlet("/user/interact")
public class InteractionServlet extends BaseServlet {
    private final InteractionDAO interactionDAO = new InteractionDAO();
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int itemId = Integer.parseInt(param(req, "itemId"));
            InteractionType type = InteractionType.valueOf(param(req, "type"));
            Optional<Item> item = itemDAO.findById(itemId);
            if (item.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "That item no longer exists.");
                return;
            }
            interactionDAO.log(user(req).getId(), itemId, type);

            String title = item.get().getTitle();
            String msg = switch (type) {
                case PURCHASE -> "You bought " + title + ". We will stop recommending it.";
                case LIKE -> "You liked " + title + ". We will show more like it.";
                default -> "Opened " + title + ".";
            };
            Html.setFlash(req, "success", msg);
            resp.sendRedirect(req.getContextPath() + "/user/dashboard");
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid item or action.");
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }
}
