package com.sams.service;

import com.sams.dao.StudentDAO;
import com.sams.model.Student;

import java.sql.SQLException;
import java.util.List;

/**
 * SERVICE LAYER — business logic for students.
 */
public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    public Student getStudentById(int id) throws SQLException {
        return studentDAO.getStudentById(id);
    }

    public List<Student> getStudentsByCourse(int courseId) throws SQLException {
        return studentDAO.getStudentsByCourse(courseId);
    }

    public boolean addStudent(Student s) throws SQLException {
        validate(s);
        return studentDAO.insertStudent(s);
    }

    public boolean updateStudent(Student s) throws SQLException {
        validate(s);
        return studentDAO.updateStudent(s);
    }

    public boolean deleteStudent(int id) throws SQLException {
        return studentDAO.deleteStudent(id);
    }

    private void validate(Student s) {
        if (s.getRegNumber() == null || s.getRegNumber().isBlank())
            throw new IllegalArgumentException("Registration number is required.");
        if (s.getFirstName() == null || s.getFirstName().isBlank())
            throw new IllegalArgumentException("First name is required.");
        if (s.getLastName() == null || s.getLastName().isBlank())
            throw new IllegalArgumentException("Last name is required.");
        if (s.getEmail() == null || s.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required.");
        if (s.getCourseId() <= 0)
            throw new IllegalArgumentException("Please select a course.");
        if (s.getEnrolledAt() == null)
            throw new IllegalArgumentException("Enrolment date is required.");
    }
}
