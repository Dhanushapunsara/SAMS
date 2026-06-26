package com.sams.model;

import java.time.LocalDate;

/**
 * MODEL — represents a row in the `students` table.
 */
public class Student {

    private int       studentId;
    private String    regNumber;
    private String    firstName;
    private String    lastName;
    private String    email;
    private String    phone;
    private int       courseId;
    private String    courseName;   // joined field for display
    private LocalDate enrolledAt;

    public Student() {}

    public Student(int studentId, String regNumber, String firstName, String lastName,
                   String email, String phone, int courseId, String courseName, LocalDate enrolledAt) {
        this.studentId  = studentId;
        this.regNumber  = regNumber;
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.email      = email;
        this.phone      = phone;
        this.courseId   = courseId;
        this.courseName = courseName;
        this.enrolledAt = enrolledAt;
    }

    public String getFullName() { return firstName + " " + lastName; }

    public int       getStudentId()                        { return studentId; }
    public void      setStudentId(int studentId)           { this.studentId = studentId; }

    public String    getRegNumber()                        { return regNumber; }
    public void      setRegNumber(String regNumber)        { this.regNumber = regNumber; }

    public String    getFirstName()                        { return firstName; }
    public void      setFirstName(String firstName)        { this.firstName = firstName; }

    public String    getLastName()                         { return lastName; }
    public void      setLastName(String lastName)          { this.lastName = lastName; }

    public String    getEmail()                            { return email; }
    public void      setEmail(String email)                { this.email = email; }

    public String    getPhone()                            { return phone; }
    public void      setPhone(String phone)                { this.phone = phone; }

    public int       getCourseId()                         { return courseId; }
    public void      setCourseId(int courseId)             { this.courseId = courseId; }

    public String    getCourseName()                       { return courseName; }
    public void      setCourseName(String courseName)      { this.courseName = courseName; }

    public LocalDate getEnrolledAt()                       { return enrolledAt; }
    public void      setEnrolledAt(LocalDate enrolledAt)   { this.enrolledAt = enrolledAt; }

    @Override
    public String toString() { return regNumber + " — " + getFullName(); }
}
