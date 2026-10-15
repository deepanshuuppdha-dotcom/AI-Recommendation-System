package com.recsys.dao;

import com.recsys.util.DBConnectionManager;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PreferenceDAO {

    /** Category -> weight (0..5) for one user. */
    public Map<String, Double> getPreferences(int userId) throws SQLException {
        Map<String, Double> prefs = new HashMap<>();
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT category, weight FROM user_preferences WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) prefs.put(rs.getString("category"), (double) rs.getInt("weight"));
            }
        }
        return prefs;
    }

    /** Saves all categories in one transaction: either every preference is stored or none. */
    public void save(int userId, Map<String, Integer> prefs) throws SQLException {
        String sql = "INSERT INTO user_preferences (user_id, category, weight) VALUES (?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE weight = VALUES(weight)";
        try (Connection c = DBConnectionManager.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (Map.Entry<String, Integer> e : prefs.entrySet()) {
                    ps.setInt(1, userId);
                    ps.setString(2, e.getKey());
                    ps.setInt(3, e.getValue());
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
}
