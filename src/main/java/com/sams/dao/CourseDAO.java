package com.sams.dao;

import com.sams.model.Course;
import com.sams.model.Subject;
import com.sams.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DATA ACCESS LAYER — CRUD for `courses` and `subjects` tables.
 */
public class CourseDAO {

    //  COURSES

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapCourse(rs));
        }
        return list;
    }

    public Course getCourseById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapCourse(rs);
        }
        return null;
    }

    public boolean insertCourse(Course c) throws SQLException {
        String sql = "INSERT INTO courses (course_code, course_name, description, duration) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getDescription());
            ps.setInt(4, c.getDuration());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateCourse(Course c) throws SQLException {
        String sql = "UPDATE courses SET course_code=?, course_name=?, description=?, duration=? WHERE course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getDescription());
            ps.setInt(4, c.getDuration());
            ps.setInt(5, c.getCourseId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    //  SUBJECTS

    public List<Subject> getAllSubjects() throws SQLException {
        List<Subject> list = new ArrayList<>();
        String sql = """
            SELECT s.*, c.course_name
            FROM subjects s
            JOIN courses c ON s.course_id = c.course_id
            ORDER BY s.subject_code
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapSubject(rs));
        }
        return list;
    }

    public List<Subject> getSubjectsByCourse(int courseId) throws SQLException {
        List<Subject> list = new ArrayList<>();
        String sql = """
            SELECT s.*, c.course_name
            FROM subjects s
            JOIN courses c ON s.course_id = c.course_id
            WHERE s.course_id = ?
            ORDER BY s.subject_code
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapSubject(rs));
        }
        return list;
    }

    public boolean insertSubject(Subject s) throws SQLException {
        String sql = "INSERT INTO subjects (subject_code, subject_name, course_id, credits) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setInt(3, s.getCourseId());
            ps.setInt(4, s.getCredits());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateSubject(Subject s) throws SQLException {
        String sql = "UPDATE subjects SET subject_code=?, subject_name=?, course_id=?, credits=? WHERE subject_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setInt(3, s.getCourseId());
            ps.setInt(4, s.getCredits());
            ps.setInt(5, s.getSubjectId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSubject(int id) throws SQLException {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Mappers ────────────────────────────────────────────────
    private Course mapCourse(ResultSet rs) throws SQLException {
        return new Course(
                rs.getInt("course_id"),
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getString("description"),
                rs.getInt("duration")
        );
    }

    private Subject mapSubject(ResultSet rs) throws SQLException {
        return new Subject(
                rs.getInt("subject_id"),
                rs.getString("subject_code"),
                rs.getString("subject_name"),
                rs.getInt("course_id"),
                rs.getString("course_name"),
                rs.getInt("credits")
        );
    }
}
