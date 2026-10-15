package com.recsys.servlet;

import com.recsys.dao.ItemDAO;
import com.recsys.dao.PreferenceDAO;
import com.recsys.exception.ValidationException;
import com.recsys.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** User dashboard: preference settings form. */
@WebServlet("/user/preferences")
public class PreferenceServlet extends BaseServlet {
    private static final String[] LEVELS = {"Not interested", "Slightly", "Somewhat", "Interested", "Very interested", "Love it"};
    private final ItemDAO itemDAO = new ItemDAO();
    private final PreferenceDAO preferenceDAO = new PreferenceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<String> categories = itemDAO.findCategories();
            Map<String, Double> current = preferenceDAO.getPreferences(user(req).getId());

            StringBuilder sb = new StringBuilder();
            sb.append("<section class=\"page-head\"><h1>Your preferences</h1>")
              .append("<p class=\"muted\">Rate each category. Higher ratings move those items up your list.</p></section>")
              .append("<form class=\"panel\" method=\"post\" action=\"").append(req.getContextPath()).append("/user/preferences\">");
            for (String cat : categories) {
                int selected = current.getOrDefault(cat, 0.0).intValue();
                sb.append("<div class=\"field-row ").append(Html.catClass(cat)).append("\">")
                  .append("<label for=\"pref_").append(Html.esc(cat)).append("\">").append(Html.esc(cat)).append("</label>")
                  .append("<select id=\"pref_").append(Html.esc(cat)).append("\" name=\"pref_").append(Html.esc(cat)).append("\">");
                for (int i = 0; i < LEVELS.length; i++) {
                    sb.append("<option value=\"").append(i).append("\"").append(i == selected ? " selected" : "")
                      .append(">").append(i).append(" - ").append(LEVELS[i]).append("</option>");
                }
                sb.append("</select></div>");
            }
            sb.append("<button class=\"btn btn-primary\" type=\"submit\">Save preferences</button></form>");
            render(req, resp, "Preferences", "preferences", sb.toString());
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, Integer> prefs = new LinkedHashMap<>();
            for (String cat : itemDAO.findCategories()) {
                prefs.put(cat, parseLevel(param(req, "pref_" + cat), cat));
            }
            preferenceDAO.save(user(req).getId(), prefs);
            Html.setFlash(req, "success", "Preferences saved. Your recommendations now reflect them.");
            resp.sendRedirect(req.getContextPath() + "/user/dashboard");
        } catch (ValidationException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    private int parseLevel(String raw, String category) throws ValidationException {
        try {
            int v = Integer.parseInt(raw);
            if (v >= 0 && v <= 5) return v;
        } catch (NumberFormatException ignored) { }
        throw new ValidationException("Pick a rating from 0 to 5 for " + category + ".");
    }
}
