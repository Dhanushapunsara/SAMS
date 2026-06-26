package com.sams.model;

/**
 * MODEL — represents a row in the `courses` table.
 */
public class Course {

    private int    courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private int    duration;

    public Course() {}

    public Course(int courseId, String courseCode, String courseName, String description, int duration) {
        this.courseId    = courseId;
        this.courseCode  = courseCode;
        this.courseName  = courseName;
        this.description = description;
        this.duration    = duration;
    }

    public int    getCourseId()                    { return courseId; }
    public void   setCourseId(int courseId)        { this.courseId = courseId; }

    public String getCourseCode()                      { return courseCode; }
    public void   setCourseCode(String courseCode)     { this.courseCode = courseCode; }

    public String getCourseName()                      { return courseName; }
    public void   setCourseName(String courseName)     { this.courseName = courseName; }

    public String getDescription()                     { return description; }
    public void   setDescription(String description)   { this.description = description; }

    public int    getDuration()                        { return duration; }
    public void   setDuration(int duration)            { this.duration = duration; }

    @Override
    public String toString() { return courseCode + " — " + courseName; }
}
