package com.sams.dao;

import com.sams.model.ClassSession;
import com.sams.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DATA ACCESS LAYER — CRUD for `class_sessions`.
 */
public class ClassSessionDAO {

    public List<ClassSession> getAllSessions() throws SQLException {
        List<ClassSession> list = new ArrayList<>();
        String sql = """
            SELECT cs.*, s.subject_name, u.full_name AS lecturer_name
            FROM class_sessions cs
            JOIN subjects  s ON cs.subject_id  = s.subject_id
            JOIN lecturers l ON cs.lecturer_id  = l.lecturer_id
            JOIN users     u ON l.user_id        = u.user_id
            ORDER BY cs.session_date DESC, cs.start_time
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapSession(rs));
        }
        return list;
    }

    public List<ClassSession> getSessionsByLecturer(int lecturerId) throws SQLException {
        List<ClassSession> list = new ArrayList<>();
        String sql = """
            SELECT cs.*, s.subject_name, u.full_name AS lecturer_name
            FROM class_sessions cs
            JOIN subjects  s ON cs.subject_id  = s.subject_id
            JOIN lecturers l ON cs.lecturer_id  = l.lecturer_id
            JOIN users     u ON l.user_id        = u.user_id
            WHERE cs.lecturer_id = ?
            ORDER BY cs.session_date DESC, cs.start_time
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapSession(rs));
        }
        return list;
    }

    public boolean insertSession(ClassSession cs) throws SQLException {
        String sql = """
            INSERT INTO class_sessions
              (subject_id, lecturer_id, session_date, start_time, end_time, venue)
            VALUES (?,?,?,?,?,?)
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cs.getSubjectId());
            ps.setInt(2, cs.getLecturerId());
            ps.setDate(3, Date.valueOf(cs.getSessionDate()));
            ps.setTime(4, Time.valueOf(cs.getStartTime()));
            ps.setTime(5, Time.valueOf(cs.getEndTime()));
            ps.setString(6, cs.getVenue());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateSession(ClassSession cs) throws SQLException {
        String sql = """
            UPDATE class_sessions
            SET subject_id=?, lecturer_id=?, session_date=?, start_time=?, end_time=?, venue=?
            WHERE session_id=?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cs.getSubjectId());
            ps.setInt(2, cs.getLecturerId());
            ps.setDate(3, Date.valueOf(cs.getSessionDate()));
            ps.setTime(4, Time.valueOf(cs.getStartTime()));
            ps.setTime(5, Time.valueOf(cs.getEndTime()));
            ps.setString(6, cs.getVenue());
            ps.setInt(7, cs.getSessionId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSession(int id) throws SQLException {
        String sql = "DELETE FROM class_sessions WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Mapper ─────────────────────────────────────────────────
    private ClassSession mapSession(ResultSet rs) throws SQLException {
        return new ClassSession(
                rs.getInt("session_id"),
                rs.getInt("subject_id"),
                rs.getString("subject_name"),
                rs.getInt("lecturer_id"),
                rs.getString("lecturer_name"),
                rs.getDate("session_date").toLocalDate(),
                rs.getTime("start_time").toLocalTime(),
                rs.getTime("end_time").toLocalTime(),
                rs.getString("venue")
        );
    }
}
