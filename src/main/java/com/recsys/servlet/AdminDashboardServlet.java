package com.recsys.servlet;

import com.recsys.model.AnalyticsReport;
import com.recsys.service.AnalyticsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    private final AnalyticsService analytics = new AnalyticsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        try {
            AnalyticsReport r = analytics.buildReport();
            String body = "<section class=\"page-head\"><h1>Admin overview</h1>"
                + "<p class=\"muted\">How the recommendation engine is doing right now.</p></section>"
                + "<div class=\"stats\">"
                + stat(String.valueOf(r.getTotalUsers()), "registered users")
                + stat(String.valueOf(r.getOverall().getShown()), "recommendations shown")
                + stat(String.format("%.1f%%", r.getOverall().getCtr()), "click-through rate")
                + stat(String.format("%.1f%%", r.getOverall().getConversionRate()), "conversion rate")
                + "</div>"
                + "<div class=\"grid two\">"
                + "<a class=\"panel linkcard\" href=\"" + ctx + "/admin/settings\"><h2>System settings</h2>"
                + "<p class=\"muted\">Choose the algorithm and set its weights.</p></a>"
                + "<a class=\"panel linkcard\" href=\"" + ctx + "/admin/analytics\"><h2>Recommendation analysis</h2>"
                + "<p class=\"muted\">See what users click and what to improve.</p></a>"
                + "</div>";
            render(req, resp, "Overview", "dashboard", body);
        } catch (SQLException e) {
            serverError(req, resp, e);
        }
    }

    private String stat(String value, String label) {
        return "<div class=\"stat\"><strong>" + value + "</strong><span>" + label + "</span></div>";
    }
}
