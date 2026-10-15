package com.recsys.servlet;

import com.recsys.model.Item;
import com.recsys.model.ScoredItem;
import com.recsys.service.RecommendationService;
import com.recsys.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** User dashboard: the personalised recommendation list. */
@WebServlet("/user/dashboard")
public class RecommendationServlet extends BaseServlet {
    private final RecommendationService service = new RecommendationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        try {
            List<ScoredItem> picks = service.recommend(user(req).getId());

            StringBuilder sb = new StringBuilder();
            sb.append("<section class=\"page-head\"><h1>Picked for you, ").append(Html.esc(user(req).getName()))
              .append("</h1><p class=\"muted\">Rankings update as you like, open and buy things. ")
              .append("<a href=\"").append(ctx).append("/user/preferences\">Adjust your preferences</a></p></section>");

            if (picks.isEmpty()) {
                sb.append("<section class=\"panel\"><h2>Nothing to recommend yet</h2>")
                  .append("<p>Set your preferences and Shelfwise will fill this page.</p>")
                  .append("<a class=\"btn btn-primary\" href=\"").append(ctx).append("/user/preferences\">Set preferences</a></section>");
            } else {
                sb.append("<div class=\"grid\">");
                for (ScoredItem si : picks) sb.append(card(ctx, si));
                sb.append("</div>");
            }
            render(req, resp, "For you", "dashboard", sb.toString());
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    private String card(String ctx, ScoredItem si) {
        Item it = si.getItem();
        int pct = (int) Math.round(si.getScore() * 100);
        int lit = (int) Math.round(si.getScore() * 10);

        StringBuilder sb = new StringBuilder();
        sb.append("<article class=\"item ").append(Html.catClass(it.getCategory())).append("\">");
        sb.append("<p class=\"cat\">").append(Html.esc(it.getCategory())).append("</p>");
        sb.append("<h3>").append(Html.esc(it.getTitle())).append("</h3>");
        sb.append("<p class=\"muted\">").append(Html.esc(it.getDescription())).append("</p>");
        sb.append("<p class=\"tags\">");
        for (String t : it.getTags().split(",")) {
            if (!t.isBlank()) sb.append("<span>").append(Html.esc(t.trim())).append("</span>");
        }
        sb.append("</p>");
        sb.append("<div class=\"meter\" role=\"img\" aria-label=\"").append(pct).append(" percent match\">");
        for (int i = 1; i <= 10; i++) sb.append(i <= lit ? "<i class=\"on\"></i>" : "<i></i>");
        sb.append("</div>");
        sb.append("<p class=\"small\"><strong>").append(pct).append("% match.</strong> ")
          .append(Html.esc(si.getReason())).append("</p>");
        sb.append("<div class=\"actions\">")
          .append(action(ctx, it.getId(), "CLICK", "Open", "btn btn-ghost"))
          .append(action(ctx, it.getId(), "LIKE", "Like", "btn btn-ghost"))
          .append(action(ctx, it.getId(), "PURCHASE", "Buy", "btn btn-primary"))
          .append("</div></article>");
        return sb.toString();
    }

    private String action(String ctx, int itemId, String type, String label, String cls) {
        return "<form method=\"post\" action=\"" + ctx + "/user/interact\">"
             + "<input type=\"hidden\" name=\"itemId\" value=\"" + itemId + "\">"
             + "<input type=\"hidden\" name=\"type\" value=\"" + type + "\">"
             + "<button class=\"" + cls + "\" type=\"submit\">" + label + "</button></form>";
    }
}
