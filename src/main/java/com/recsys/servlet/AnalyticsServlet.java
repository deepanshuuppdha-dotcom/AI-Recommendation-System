package com.recsys.servlet;

import com.recsys.model.AnalyticsReport;
import com.recsys.model.AnalyticsReport.Row;
import com.recsys.service.AnalyticsService;
import com.recsys.util.Html;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** Admin: performance report with CSS-only bar graphs. */
@WebServlet("/admin/analytics")
public class AnalyticsServlet extends BaseServlet {
    private final AnalyticsService service = new AnalyticsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            AnalyticsReport r = service.buildReport();
            Row o = r.getOverall();

            StringBuilder sb = new StringBuilder();
            sb.append("<section class=\"page-head\"><h1>Recommendation analysis</h1>")
              .append("<p class=\"muted\">Based on every recommendation shown to users so far.</p></section>");

            sb.append("<div class=\"stats\">")
              .append(stat(String.valueOf(o.getShown()), "shown"))
              .append(stat(String.valueOf(o.getClicks()), "clicked"))
              .append(stat(String.valueOf(o.getConversions()), "purchased"))
              .append(stat(String.format("%.1f%%", o.getCtr()), "click-through rate"))
              .append("</div>");

            sb.append("<section class=\"panel\"><h2>What to improve</h2><ul class=\"tips\">");
            for (String tip : r.getSuggestions()) sb.append("<li>").append(Html.esc(tip)).append("</li>");
            sb.append("</ul></section>");

            sb.append("<section class=\"panel\"><h2>Click-through rate by category</h2>")
              .append(table(r.getByCategory(), "Category", true)).append("</section>");
            sb.append("<section class=\"panel\"><h2>Most clicked items</h2>")
              .append(table(r.getTopItems(), "Item", false)).append("</section>");

            render(req, resp, "Analytics", "analytics", sb.toString());
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    private String table(List<Row> rows, String firstHeader, boolean colourByCategory) {
        if (rows.isEmpty()) return "<p class=\"muted\">No data yet. Recommendations appear here once users open their dashboard.</p>";
        StringBuilder sb = new StringBuilder("<div class=\"scroll\"><table><thead><tr><th>")
            .append(firstHeader).append("</th><th>Shown</th><th>Clicked</th><th>Purchased</th><th class=\"wide\">CTR</th></tr></thead><tbody>");
        for (Row row : rows) {
            double ctr = Math.min(100, row.getCtr());
            sb.append("<tr class=\"").append(colourByCategory ? Html.catClass(row.getLabel()) : "").append("\"><td>")
              .append(Html.esc(row.getLabel())).append("</td><td>").append(row.getShown())
              .append("</td><td>").append(row.getClicks()).append("</td><td>").append(row.getConversions())
              .append("</td><td><div class=\"bar\"><span style=\"width:").append(String.format("%.0f", ctr))
              .append("%\"></span></div><small>").append(String.format("%.1f%%", row.getCtr())).append("</small></td></tr>");
        }
        return sb.append("</tbody></table></div>").toString();
    }

    private String stat(String value, String label) {
        return "<div class=\"stat\"><strong>" + value + "</strong><span>" + label + "</span></div>";
    }
}
