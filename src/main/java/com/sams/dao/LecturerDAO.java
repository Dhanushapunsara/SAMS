package com.sams.dao;

import com.sams.model.Lecturer;
import com.sams.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DATA ACCESS LAYER — CRUD for the `lecturers` table (joined with `users`).
 */
public class LecturerDAO {

    public List<Lecturer> getAllLecturers() throws SQLException {
        List<Lecturer> list = new ArrayList<>();
        String sql = """
            SELECT l.*, u.full_name, u.username
            FROM lecturers l
            JOIN users u ON l.user_id = u.user_id
            ORDER BY l.employee_id
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapLecturer(rs));
        }
        return list;
    }

    public Lecturer getLecturerById(int id) throws SQLException {
        String sql = """
            SELECT l.*, u.full_name, u.username
            FROM lecturers l
            JOIN users u ON l.user_id = u.user_id
            WHERE l.lecturer_id = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapLecturer(rs);
        }
        return null;
    }

    public Lecturer getLecturerByUserId(int userId) throws SQLException {
        String sql = """
            SELECT l.*, u.full_name, u.username
            FROM lecturers l
            JOIN users u ON l.user_id = u.user_id
            WHERE l.user_id = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapLecturer(rs);
        }
        return null;
    }

    /**
     * Inserts into both `users` and `lecturers` tables in one transaction.
     */
    public boolean insertLecturer(Lecturer l) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            // 1. insert user
            String userSql = "INSERT INTO users (username, password, full_name, role) VALUES (?,?,?,'LECTURER')";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, l.getUsername());
                ps.setString(2, l.getPassword());
                ps.setString(3, l.getFullName());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                userId = keys.getInt(1);
            }
            // 2. insert lecturer
            String lecSql = "INSERT INTO lecturers (user_id, employee_id, department) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(lecSql)) {
                ps.setInt(1, userId);
                ps.setString(2, l.getEmployeeId());
                ps.setString(3, l.getDepartment());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public boolean updateLecturer(Lecturer l) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            // update user
            String userSql = "UPDATE users SET full_name=?, password=? WHERE user_id=?";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, l.getFullName());
                ps.setString(2, l.getPassword());
                ps.setInt(3, l.getUserId());
                ps.executeUpdate();
            }
            // update lecturer
            String lecSql = "UPDATE lecturers SET employee_id=?, department=? WHERE lecturer_id=?";
            try (PreparedStatement ps = conn.prepareStatement(lecSql)) {
                ps.setString(1, l.getEmployeeId());
                ps.setString(2, l.getDepartment());
                ps.setInt(3, l.getLecturerId());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /** Deleting the user cascades to lecturers via FK. */
    public boolean deleteLecturer(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // Subject assignments
    public boolean assignSubject(int lecturerId, int subjectId) throws SQLException {
        String sql = "INSERT IGNORE INTO lecturer_subjects (lecturer_id, subject_id) VALUES (?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeSubject(int lecturerId, int subjectId) throws SQLException {
        String sql = "DELETE FROM lecturer_subjects WHERE lecturer_id=? AND subject_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ps.setInt(2, subjectId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Mapper ─────────────────────────────────────────────────
    private Lecturer mapLecturer(ResultSet rs) throws SQLException {
        return new Lecturer(
                rs.getInt("lecturer_id"),
                rs.getInt("user_id"),
                rs.getString("employee_id"),
                rs.getString("department"),
                rs.getString("full_name"),
                rs.getString("username")
        );
    }
}
