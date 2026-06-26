package com.sams.dao;

import com.sams.model.User;
import com.sams.util.DBConnection;

import java.sql.*;

/**
 * DATA ACCESS LAYER — all SQL operations for the `users` table.
 */
public class UserDAO {

    /**
     * Validate login credentials and return the matching User, or null if not found.
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    /**
     * Insert a new user row and return the generated user_id.
     */
    public int insertUser(String username, String password, String fullName, User.Role role) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, role.name());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    /**
     * Update an existing user's details.
     */
    public boolean updateUser(int userId, String fullName, String password) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, password = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, password);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Map a ResultSet row → User object. */
    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                User.Role.valueOf(rs.getString("role"))
        );
    }
}
