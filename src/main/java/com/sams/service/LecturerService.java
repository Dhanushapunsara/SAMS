package com.sams.service;

import com.sams.dao.LecturerDAO;
import com.sams.model.Lecturer;

import java.sql.SQLException;
import java.util.List;

/**
 * SERVICE LAYER — business logic for lecturers.
 */
public class LecturerService {

    private final LecturerDAO lecturerDAO = new LecturerDAO();

    public List<Lecturer> getAllLecturers() throws SQLException {
        return lecturerDAO.getAllLecturers();
    }

    public Lecturer getLecturerById(int id) throws SQLException {
        return lecturerDAO.getLecturerById(id);
    }

    public Lecturer getLecturerByUserId(int userId) throws SQLException {
        return lecturerDAO.getLecturerByUserId(userId);
    }

    public boolean addLecturer(Lecturer l) throws SQLException {
        validate(l);
        return lecturerDAO.insertLecturer(l);
    }

    public boolean updateLecturer(Lecturer l) throws SQLException {
        validate(l);
        return lecturerDAO.updateLecturer(l);
    }

    public boolean deleteLecturer(int userId) throws SQLException {
        return lecturerDAO.deleteLecturer(userId);
    }

    public boolean assignSubject(int lecturerId, int subjectId) throws SQLException {
        return lecturerDAO.assignSubject(lecturerId, subjectId);
    }

    public boolean removeSubject(int lecturerId, int subjectId) throws SQLException {
        return lecturerDAO.removeSubject(lecturerId, subjectId);
    }

    private void validate(Lecturer l) {
        if (l.getEmployeeId() == null || l.getEmployeeId().isBlank())
            throw new IllegalArgumentException("Employee ID is required.");
        if (l.getFullName() == null || l.getFullName().isBlank())
            throw new IllegalArgumentException("Full name is required.");
        if (l.getUsername() == null || l.getUsername().isBlank())
            throw new IllegalArgumentException("Username is required.");
        if (l.getPassword() == null || l.getPassword().isBlank())
            throw new IllegalArgumentException("Password is required.");
    }
}
