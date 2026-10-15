package com.recsys.servlet;

import com.recsys.exception.InvalidSettingException;
import com.recsys.model.AlgorithmSettings;
import com.recsys.model.StrategyType;
import com.recsys.service.SettingsService;
import com.recsys.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Admin: system settings form (algorithm, weights, list size). */
@WebServlet("/admin/settings")
public class SettingsServlet extends BaseServlet {
    private static final String[] KEYS = {AlgorithmSettings.KEY_STRATEGY, AlgorithmSettings.KEY_W_PREF,
            AlgorithmSettings.KEY_W_BEHAVIOR, AlgorithmSettings.KEY_W_POPULARITY, AlgorithmSettings.KEY_TOP_N};
    private final SettingsService service = new SettingsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            form(req, resp, service.load().toMap(), "");
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> raw = new LinkedHashMap<>();
        for (String key : KEYS) raw.put(key, param(req, key));
        try {
            service.update(raw);
            Html.setFlash(req, "success", "Settings updated. New recommendations use them immediately.");
            resp.sendRedirect(req.getContextPath() + "/admin/settings");
        } catch (InvalidSettingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            form(req, resp, raw, e.getMessage());
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    private void form(HttpServletRequest req, HttpServletResponse resp, Map<String, String> v, String error)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<section class=\"page-head\"><h1>System settings</h1>")
          .append("<p class=\"muted\">These values control how every user's list is ranked.</p></section>");
        if (!error.isEmpty()) sb.append(Html.alert("error", error));
        sb.append("<form class=\"panel\" method=\"post\" action=\"").append(req.getContextPath()).append("/admin/settings\">");

        sb.append("<fieldset><legend>Recommendation strategy</legend>");
        String current = v.getOrDefault(AlgorithmSettings.KEY_STRATEGY, "HYBRID");
        for (StrategyType t : StrategyType.values()) {
            sb.append("<label class=\"radio\"><input type=\"radio\" name=\"strategy\" value=\"").append(t.name()).append("\"")
              .append(t.name().equals(current) ? " checked" : "").append("> ").append(Html.esc(t.getLabel())).append("</label>");
        }
        sb.append("</fieldset>");

        sb.append("<fieldset><legend>Hybrid weights (0 to 1)</legend><div class=\"three\">")
          .append(number("weight_preference", "Preference", v, "0", "1", "0.05"))
          .append(number("weight_behavior", "Behavior", v, "0", "1", "0.05"))
          .append(number("weight_popularity", "Popularity", v, "0", "1", "0.05"))
          .append("</div><p class=\"small muted\">Weights are relative: 0.5 / 0.3 / 0.2 ranks the same as 5 / 3 / 2.</p></fieldset>");

        sb.append("<fieldset><legend>List size</legend>")
          .append(number("top_n", "Recommendations per user", v, "1", "20", "1"))
          .append("</fieldset>");

        sb.append("<button class=\"btn btn-primary\" type=\"submit\">Save settings</button></form>");
        render(req, resp, "Settings", "settings", sb.toString());
    }

    private String number(String name, String label, Map<String, String> v, String min, String max, String step) {
        return "<div><label for=\"" + name + "\">" + label + "</label><input id=\"" + name + "\" name=\"" + name
             + "\" type=\"number\" min=\"" + min + "\" max=\"" + max + "\" step=\"" + step + "\" required value=\""
             + Html.esc(v.getOrDefault(name, "")) + "\"></div>";
    }
}
