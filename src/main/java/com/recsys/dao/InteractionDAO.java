package com.recsys.dao;

import com.recsys.model.InteractionType;
import com.recsys.util.DBConnectionManager;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InteractionDAO {

    /**
     * Records an interaction and flags the user's latest matching recommendation as clicked
     * (and converted, for purchases). Both writes happen in one transaction.
     */
    public void log(int userId, int itemId, InteractionType type) throws SQLException {
        String insert = "INSERT INTO interactions (user_id, item_id, type) VALUES (?, ?, ?)";
        String mark = "UPDATE recommendations_log SET clicked = TRUE, converted = (converted OR ?) "
                    + "WHERE user_id = ? AND item_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection c = DBConnectionManager.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ins = c.prepareStatement(insert);
                 PreparedStatement upd = c.prepareStatement(mark)) {
                ins.setInt(1, userId);
                ins.setInt(2, itemId);
                ins.setString(3, type.name());
                ins.executeUpdate();

                upd.setBoolean(1, type == InteractionType.PURCHASE);
                upd.setInt(2, userId);
                upd.setInt(3, itemId);
                upd.executeUpdate();
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    /** Category -> accumulated behavior score (sum of interaction weights). */
    public Map<String, Double> getBehaviorScores(int userId) throws SQLException {
        String sql = "SELECT i.category, ia.type, COUNT(*) AS n FROM interactions ia "
                   + "JOIN items i ON i.id = ia.item_id WHERE ia.user_id = ? GROUP BY i.category, ia.type";
        Map<String, Double> scores = new HashMap<>();
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InteractionType t = InteractionType.valueOf(rs.getString("type"));
                    scores.merge(rs.getString("category"), (double) t.getWeight() * rs.getInt("n"), Double::sum);
                }
            }
        }
        return scores;
    }

    public Set<Integer> getPurchasedItemIds(int userId) throws SQLException {
        Set<Integer> ids = new HashSet<>();
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT DISTINCT item_id FROM interactions WHERE user_id = ? AND type = 'PURCHASE'")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        }
        return ids;
    }
}
