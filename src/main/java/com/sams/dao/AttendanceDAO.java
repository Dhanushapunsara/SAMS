package com.sams.dao;

import com.sams.model.Attendance;
import com.sams.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DATA ACCESS LAYER — attendance marking and report queries.
 */
public class AttendanceDAO {

    /** All attendance rows for a given session (for marking). */
    public List<Attendance> getAttendanceBySession(int sessionId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = """
            SELECT a.*, CONCAT(st.first_name,' ',st.last_name) AS student_name, st.reg_number
            FROM attendance a
            JOIN students st ON a.student_id = st.student_id
            WHERE a.session_id = ?
            ORDER BY st.reg_number
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAttendance(rs));
        }
        return list;
    }

    /** Get all students for a session (pre-populate the attendance sheet). */
    public List<Attendance> getAttendanceSheetForSession(int sessionId) throws SQLException {
        // First get the course of this session, then get all its students
        String sql = """
            SELECT
                IFNULL(a.attendance_id, 0)    AS attendance_id,
                cs.session_id,
                st.student_id,
                CONCAT(st.first_name,' ',st.last_name) AS student_name,
                st.reg_number,
                IFNULL(a.status, 'ABSENT')    AS status,
                IFNULL(a.remarks, '')          AS remarks,
                a.marked_at
            FROM class_sessions cs
            JOIN subjects sub  ON cs.subject_id = sub.subject_id
            JOIN students  st  ON st.course_id  = sub.course_id
            LEFT JOIN attendance a
                ON a.session_id = cs.session_id AND a.student_id = st.student_id
            WHERE cs.session_id = ?
            ORDER BY st.reg_number
            """;
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAttendance(rs));
        }
        return list;
    }

    /** Save (insert or update) one attendance row. */
    public boolean saveAttendance(Attendance a) throws SQLException {
        String sql = """
            INSERT INTO attendance (session_id, student_id, status, remarks)
            VALUES (?,?,?,?)
            ON DUPLICATE KEY UPDATE status=VALUES(status), remarks=VALUES(remarks)
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getSessionId());
            ps.setInt(2, a.getStudentId());
            ps.setString(3, a.getStatus().name());
            ps.setString(4, a.getRemarks());
            return ps.executeUpdate() > 0;
        }
    }

    //  REPORT QUERIES

    /**
     * Full attendance report, filterable by student, subject, and/or date range.
     * Pass -1 / null to skip a filter.
     */
    public List<Attendance> getReport(int studentId, int subjectId,
                                      LocalDate fromDate, LocalDate toDate) throws SQLException {
        StringBuilder sb = new StringBuilder("""
            SELECT a.*,
                   CONCAT(st.first_name,' ',st.last_name) AS student_name,
                   st.reg_number,
                   cs.session_date
            FROM attendance a
            JOIN students      st  ON a.student_id  = st.student_id
            JOIN class_sessions cs ON a.session_id   = cs.session_id
            JOIN subjects       sub ON cs.subject_id  = sub.subject_id
            WHERE 1=1
            """);
        List<Object> params = new ArrayList<>();

        if (studentId > 0) {
            sb.append(" AND a.student_id = ?");
            params.add(studentId);
        }
        if (subjectId > 0) {
            sb.append(" AND cs.subject_id = ?");
            params.add(subjectId);
        }
        if (fromDate != null) {
            sb.append(" AND cs.session_date >= ?");
            params.add(Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sb.append(" AND cs.session_date <= ?");
            params.add(Date.valueOf(toDate));
        }
        sb.append(" ORDER BY cs.session_date DESC, st.reg_number");

        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else ps.setDate(i + 1, (Date) p);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attendance att = mapAttendance(rs);
                // also set session date on the object for display (reuse remarks field or subclass)
                list.add(att);
            }
        }
        return list;
    }

    // ── Mapper ─────────────────────────────────────────────────
    private Attendance mapAttendance(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("marked_at");
        return new Attendance(
                rs.getInt("attendance_id"),
                rs.getInt("session_id"),
                rs.getInt("student_id"),
                rs.getString("student_name"),
                rs.getString("reg_number"),
                Attendance.Status.valueOf(rs.getString("status")),
                rs.getString("remarks"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}
