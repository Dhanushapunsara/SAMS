package com.sams.dao;

import com.sams.model.Student;
import com.sams.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DATA ACCESS LAYER — CRUD for the `students` table.
 */
public class StudentDAO {

    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = """
            SELECT s.*, c.course_name
            FROM students s
            JOIN courses c ON s.course_id = c.course_id
            ORDER BY s.reg_number
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudent(rs));
        }
        return list;
    }

    public Student getStudentById(int id) throws SQLException {
        String sql = """
            SELECT s.*, c.course_name
            FROM students s
            JOIN courses c ON s.course_id = c.course_id
            WHERE s.student_id = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapStudent(rs);
        }
        return null;
    }

    public List<Student> getStudentsByCourse(int courseId) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = """
            SELECT s.*, c.course_name
            FROM students s
            JOIN courses c ON s.course_id = c.course_id
            WHERE s.course_id = ?
            ORDER BY s.reg_number
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapStudent(rs));
        }
        return list;
    }

    public boolean insertStudent(Student s) throws SQLException {
        String sql = """
            INSERT INTO students
              (reg_number, first_name, last_name, email, phone, course_id, enrolled_at)
            VALUES (?,?,?,?,?,?,?)
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getRegNumber());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setInt(6, s.getCourseId());
            ps.setDate(7, Date.valueOf(s.getEnrolledAt()));
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStudent(Student s) throws SQLException {
        String sql = """
            UPDATE students
            SET reg_number=?, first_name=?, last_name=?, email=?, phone=?, course_id=?, enrolled_at=?
            WHERE student_id=?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getRegNumber());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setInt(6, s.getCourseId());
            ps.setDate(7, Date.valueOf(s.getEnrolledAt()));
            ps.setInt(8, s.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Mapper ─────────────────────────────────────────────────
    private Student mapStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("student_id"),
                rs.getString("reg_number"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getInt("course_id"),
                rs.getString("course_name"),
                rs.getDate("enrolled_at").toLocalDate()
        );
    }
}
