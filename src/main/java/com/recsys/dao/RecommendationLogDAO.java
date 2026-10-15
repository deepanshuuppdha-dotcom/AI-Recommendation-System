package com.recsys.dao;

import com.recsys.model.AnalyticsReport.Row;
import com.recsys.model.ScoredItem;
import com.recsys.util.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecommendationLogDAO {

    /** Logs every recommendation shown to a user (one transaction, batched). */
    public void logShown(int userId, List<ScoredItem> shown) throws SQLException {
        if (shown.isEmpty()) return;
        String sql = "INSERT INTO recommendations_log (user_id, item_id, score) VALUES (?, ?, ?)";
        try (Connection c = DBConnectionManager.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (ScoredItem si : shown) {
                    ps.setInt(1, userId);
                    ps.setInt(2, si.getItem().getId());
                    ps.setDouble(3, si.getScore());
                    ps.addBatch();
                }
                ps.executeBatch();
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    public Row overall() throws SQLException {
        String sql = "SELECT COUNT(*), COALESCE(SUM(clicked), 0), COALESCE(SUM(converted), 0) FROM recommendations_log";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return new Row("All recommendations", rs.getLong(1), rs.getLong(2), rs.getLong(3));
        }
    }

    public List<Row> byCategory() throws SQLException {
        String sql = "SELECT i.category, COUNT(*), SUM(r.clicked), SUM(r.converted) FROM recommendations_log r "
                   + "JOIN items i ON i.id = r.item_id GROUP BY i.category ORDER BY COUNT(*) DESC";
        return rows(sql, 0);
    }

    public List<Row> topItems(int limit) throws SQLException {
        String sql = "SELECT i.title, COUNT(*), SUM(r.clicked), SUM(r.converted) FROM recommendations_log r "
                   + "JOIN items i ON i.id = r.item_id GROUP BY i.id, i.title "
                   + "ORDER BY SUM(r.clicked) DESC, COUNT(*) DESC LIMIT ?";
        return rows(sql, limit);
    }

    private List<Row> rows(String sql, int limit) throws SQLException {
        List<Row> rows = new ArrayList<>();
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (limit > 0) ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rows.add(new Row(rs.getString(1), rs.getLong(2), rs.getLong(3), rs.getLong(4)));
            }
        }
        return rows;
    }
}
