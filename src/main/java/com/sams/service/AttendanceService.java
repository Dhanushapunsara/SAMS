package com.sams.service;

import com.sams.dao.AttendanceDAO;
import com.sams.model.Attendance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * SERVICE LAYER — business logic for attendance marking and reporting.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    /** Prepopulated attendance sheet for a session (with all enrolled students). */
    public List<Attendance> getAttendanceSheet(int sessionId) throws SQLException {
        return attendanceDAO.getAttendanceSheetForSession(sessionId);
    }

    /** Save a single attendance record (insert or update). */
    public boolean saveAttendance(Attendance a) throws SQLException {
        return attendanceDAO.saveAttendance(a);
    }

    /** Save an entire session's attendance list. */
    public void saveAll(List<Attendance> list) throws SQLException {
        for (Attendance a : list) {
            attendanceDAO.saveAttendance(a);
        }
    }

    /**
     * Get the filtered attendance report.
     * Pass -1 for studentId / subjectId to skip those filters.
     * Pass null for fromDate / toDate to skip date filters.
     */
    public List<Attendance> getReport(int studentId, int subjectId,
                                      LocalDate fromDate, LocalDate toDate) throws SQLException {
        return attendanceDAO.getReport(studentId, subjectId, fromDate, toDate);
    }
}
