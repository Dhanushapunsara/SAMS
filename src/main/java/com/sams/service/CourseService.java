package com.sams.service;

import com.sams.dao.CourseDAO;
import com.sams.model.Course;
import com.sams.model.Subject;

import java.sql.SQLException;
import java.util.List;

/**
 * SERVICE LAYER — business logic for courses and subjects.
 */
public class CourseService {

    private final CourseDAO courseDAO = new CourseDAO();

    // ── Courses ────────────────────────────────────────────────

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.getAllCourses();
    }

    public Course getCourseById(int id) throws SQLException {
        return courseDAO.getCourseById(id);
    }

    public boolean addCourse(Course c) throws SQLException {
        validateCourse(c);
        return courseDAO.insertCourse(c);
    }

    public boolean updateCourse(Course c) throws SQLException {
        validateCourse(c);
        return courseDAO.updateCourse(c);
    }

    public boolean deleteCourse(int id) throws SQLException {
        return courseDAO.deleteCourse(id);
    }

    // ── Subjects ───────────────────────────────────────────────

    public List<Subject> getAllSubjects() throws SQLException {
        return courseDAO.getAllSubjects();
    }

    public List<Subject> getSubjectsByCourse(int courseId) throws SQLException {
        return courseDAO.getSubjectsByCourse(courseId);
    }

    public boolean addSubject(Subject s) throws SQLException {
        validateSubject(s);
        return courseDAO.insertSubject(s);
    }

    public boolean updateSubject(Subject s) throws SQLException {
        validateSubject(s);
        return courseDAO.updateSubject(s);
    }

    public boolean deleteSubject(int id) throws SQLException {
        return courseDAO.deleteSubject(id);
    }

    // ── Validation ─────────────────────────────────────────────

    private void validateCourse(Course c) {
        if (c.getCourseCode() == null || c.getCourseCode().isBlank())
            throw new IllegalArgumentException("Course code is required.");
        if (c.getCourseName() == null || c.getCourseName().isBlank())
            throw new IllegalArgumentException("Course name is required.");
        if (c.getDuration() <= 0)
            throw new IllegalArgumentException("Duration must be greater than 0.");
    }

    private void validateSubject(Subject s) {
        if (s.getSubjectCode() == null || s.getSubjectCode().isBlank())
            throw new IllegalArgumentException("Subject code is required.");
        if (s.getSubjectName() == null || s.getSubjectName().isBlank())
            throw new IllegalArgumentException("Subject name is required.");
        if (s.getCourseId() <= 0)
            throw new IllegalArgumentException("Please select a course.");
    }
}
