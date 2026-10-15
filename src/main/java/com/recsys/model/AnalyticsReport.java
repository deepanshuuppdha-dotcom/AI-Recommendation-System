package com.recsys.model;

import java.util.List;

public class AnalyticsReport {

    /** One line of statistics (overall, a category, or a single item). */
    public static class Row {
        private final String label;
        private final long shown;
        private final long clicks;
        private final long conversions;

        public Row(String label, long shown, long clicks, long conversions) {
            this.label = label;
            this.shown = shown;
            this.clicks = clicks;
            this.conversions = conversions;
        }
        public String getLabel() { return label; }
        public long getShown() { return shown; }
        public long getClicks() { return clicks; }
        public long getConversions() { return conversions; }
        /** Click-through rate in percent. */
        public double getCtr() { return shown == 0 ? 0 : clicks * 100.0 / shown; }
        /** Conversion rate in percent (purchases per recommendation shown). */
        public double getConversionRate() { return shown == 0 ? 0 : conversions * 100.0 / shown; }
    }

    private final int totalUsers;
    private final Row overall;
    private final List<Row> byCategory;
    private final List<Row> topItems;
    private final List<String> suggestions;

    public AnalyticsReport(int totalUsers, Row overall, List<Row> byCategory, List<Row> topItems, List<String> suggestions) {
        this.totalUsers = totalUsers;
        this.overall = overall;
        this.byCategory = byCategory;
        this.topItems = topItems;
        this.suggestions = suggestions;
    }

    public int getTotalUsers() { return totalUsers; }
    public Row getOverall() { return overall; }
    public List<Row> getByCategory() { return byCategory; }
    public List<Row> getTopItems() { return topItems; }
    public List<String> getSuggestions() { return suggestions; }
}
