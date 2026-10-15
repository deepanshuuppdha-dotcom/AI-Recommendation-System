package com.recsys.service;

import com.recsys.dao.RecommendationLogDAO;
import com.recsys.dao.UserDAO;
import com.recsys.model.AnalyticsReport;
import com.recsys.model.AnalyticsReport.Row;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsService {
    private final RecommendationLogDAO logDAO = new RecommendationLogDAO();
    private final UserDAO userDAO = new UserDAO();

    public AnalyticsReport buildReport() throws SQLException {
        Row overall = logDAO.overall();
        List<Row> byCategory = logDAO.byCategory();
        return new AnalyticsReport(userDAO.count(), overall, byCategory, logDAO.topItems(5),
                suggest(overall, byCategory));
    }

    /** Simple rule-based improvement hints for the admin. */
    private List<String> suggest(Row overall, List<Row> byCategory) {
        List<String> tips = new ArrayList<>();
        if (overall.getShown() == 0) {
            tips.add("No recommendations have been shown yet. Sign in as a user to generate some data.");
            return tips;
        }
        double ctr = overall.getCtr();
        if (ctr < 10) {
            tips.add(String.format("Click-through rate is %.1f%%, below 10%%. Raise the behavior weight so "
                    + "suggestions follow what users actually click.", ctr));
        } else if (ctr < 25) {
            tips.add(String.format("Click-through rate is %.1f%%. Try shifting weight from popularity to "
                    + "preference to personalise further.", ctr));
        } else {
            tips.add(String.format("Click-through rate is a healthy %.1f%%. Keep the current parameters.", ctr));
        }
        if (overall.getConversionRate() < 2) {
            tips.add(String.format("Conversion rate is %.1f%%. Increase the preference weight so purchases "
                    + "match stated interests.", overall.getConversionRate()));
        }
        for (Row r : byCategory) {
            if (r.getShown() >= 5 && r.getCtr() < 5) {
                tips.add(r.getLabel() + " is shown often but rarely clicked (" + String.format("%.1f", r.getCtr())
                        + "%). Review the items and tags in that category.");
            }
        }
        return tips;
    }
}
