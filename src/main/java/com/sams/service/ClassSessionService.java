package com.sams.service;

import com.sams.dao.ClassSessionDAO;
import com.sams.model.ClassSession;

import java.sql.SQLException;
import java.util.List;

/**
 * SERVICE LAYER — business logic for class sessions.
 */
public class ClassSessionService {

    private final ClassSessionDAO sessionDAO = new ClassSessionDAO();

    public List<ClassSession> getAllSessions() throws SQLException {
        return sessionDAO.getAllSessions();
    }

    public List<ClassSession> getSessionsByLecturer(int lecturerId) throws SQLException {
        return sessionDAO.getSessionsByLecturer(lecturerId);
    }

    public boolean addSession(ClassSession cs) throws SQLException {
        validate(cs);
        return sessionDAO.insertSession(cs);
    }

    public boolean updateSession(ClassSession cs) throws SQLException {
        validate(cs);
        return sessionDAO.updateSession(cs);
    }

    public boolean deleteSession(int id) throws SQLException {
        return sessionDAO.deleteSession(id);
    }

    private void validate(ClassSession cs) {
        if (cs.getSubjectId() <= 0)
            throw new IllegalArgumentException("Please select a subject.");
        if (cs.getLecturerId() <= 0)
            throw new IllegalArgumentException("Please select a lecturer.");
        if (cs.getSessionDate() == null)
            throw new IllegalArgumentException("Session date is required.");
        if (cs.getStartTime() == null || cs.getEndTime() == null)
            throw new IllegalArgumentException("Start and end times are required.");
        if (!cs.getEndTime().isAfter(cs.getStartTime()))
            throw new IllegalArgumentException("End time must be after start time.");
    }
}
