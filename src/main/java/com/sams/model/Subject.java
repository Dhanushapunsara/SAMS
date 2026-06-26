package com.sams.model;

/**
 * MODEL — represents a row in the `subjects` table.
 */
public class Subject {

    private int    subjectId;
    private String subjectCode;
    private String subjectName;
    private int    courseId;
    private String courseName;  // joined field for display
    private int    credits;

    public Subject() {}

    public Subject(int subjectId, String subjectCode, String subjectName,
                   int courseId, String courseName, int credits) {
        this.subjectId   = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.courseId    = courseId;
        this.courseName  = courseName;
        this.credits     = credits;
    }

    public int    getSubjectId()                       { return subjectId; }
    public void   setSubjectId(int subjectId)          { this.subjectId = subjectId; }

    public String getSubjectCode()                         { return subjectCode; }
    public void   setSubjectCode(String subjectCode)       { this.subjectCode = subjectCode; }

    public String getSubjectName()                         { return subjectName; }
    public void   setSubjectName(String subjectName)       { this.subjectName = subjectName; }

    public int    getCourseId()                        { return courseId; }
    public void   setCourseId(int courseId)            { this.courseId = courseId; }

    public String getCourseName()                      { return courseName; }
    public void   setCourseName(String courseName)     { this.courseName = courseName; }

    public int    getCredits()                         { return credits; }
    public void   setCredits(int credits)              { this.credits = credits; }

    @Override
    public String toString() { return subjectCode + " — " + subjectName; }
}
