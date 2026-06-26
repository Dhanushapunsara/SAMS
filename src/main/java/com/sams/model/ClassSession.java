package com.sams.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * MODEL — represents a row in the `class_sessions` table.
 */
public class ClassSession {

    private int       sessionId;
    private int       subjectId;
    private String    subjectName;   // joined
    private int       lecturerId;
    private String    lecturerName;  // joined
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String    venue;

    public ClassSession() {}

    public ClassSession(int sessionId, int subjectId, String subjectName,
                        int lecturerId, String lecturerName,
                        LocalDate sessionDate, LocalTime startTime,
                        LocalTime endTime, String venue) {
        this.sessionId    = sessionId;
        this.subjectId    = subjectId;
        this.subjectName  = subjectName;
        this.lecturerId   = lecturerId;
        this.lecturerName = lecturerName;
        this.sessionDate  = sessionDate;
        this.startTime    = startTime;
        this.endTime      = endTime;
        this.venue        = venue;
    }

    public int       getSessionId()                        { return sessionId; }
    public void      setSessionId(int sessionId)           { this.sessionId = sessionId; }

    public int       getSubjectId()                        { return subjectId; }
    public void      setSubjectId(int subjectId)           { this.subjectId = subjectId; }

    public String    getSubjectName()                      { return subjectName; }
    public void      setSubjectName(String subjectName)    { this.subjectName = subjectName; }

    public int       getLecturerId()                       { return lecturerId; }
    public void      setLecturerId(int lecturerId)         { this.lecturerId = lecturerId; }

    public String    getLecturerName()                     { return lecturerName; }
    public void      setLecturerName(String lecturerName)  { this.lecturerName = lecturerName; }

    public LocalDate getSessionDate()                      { return sessionDate; }
    public void      setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime()                        { return startTime; }
    public void      setStartTime(LocalTime startTime)     { this.startTime = startTime; }

    public LocalTime getEndTime()                          { return endTime; }
    public void      setEndTime(LocalTime endTime)         { this.endTime = endTime; }

    public String    getVenue()                            { return venue; }
    public void      setVenue(String venue)                { this.venue = venue; }

    @Override
    public String toString() {
        return subjectName + " | " + sessionDate + " " + startTime;
    }
}
