package com.recsys.dao;

import com.recsys.util.DBConnectionManager;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SettingsDAO {

    public Map<String, String> getAll() throws SQLException {
        Map<String, String> m = new HashMap<>();
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT param_name, param_value FROM system_settings");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) m.put(rs.getString(1), rs.getString(2));
        }
        return m;
    }

    /** Updates every setting atomically. */
    public void saveAll(Map<String, String> settings) throws SQLException {
        String sql = "INSERT INTO system_settings (param_name, param_value) VALUES (?, ?) "
                   + "ON DUPLICATE KEY UPDATE param_value = VALUES(param_value)";
        try (Connection c = DBConnectionManager.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (Map.Entry<String, String> e : settings.entrySet()) {
                    ps.setString(1, e.getKey());
                    ps.setString(2, e.getValue());
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
