package com.recsys.dao;

import com.recsys.model.Item;
import com.recsys.util.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemDAO {

    public List<Item> findAll() throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, title, category, tags, description, popularity FROM items ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) items.add(map(rs));
        }
        return items;
    }

    public Optional<Item> findById(int id) throws SQLException {
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, title, category, tags, description, popularity FROM items WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<String> findCategories() throws SQLException {
        List<String> cats = new ArrayList<>();
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT DISTINCT category FROM items ORDER BY category");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cats.add(rs.getString(1));
        }
        return cats;
    }

    private Item map(ResultSet rs) throws SQLException {
        return new Item(rs.getInt("id"), rs.getString("title"), rs.getString("category"),
                rs.getString("tags"), rs.getString("description"), rs.getInt("popularity"));
    }
}
