package com.sams.model;

import java.time.LocalDateTime;

/**
 * MODEL — represents a row in the `attendance` table.
 */
public class Attendance {

    public enum Status { PRESENT, ABSENT, LATE }

    private int           attendanceId;
    private int           sessionId;
    private int           studentId;
    private String        studentName;    // joined
    private String        regNumber;      // joined
    private Status        status;
    private String        remarks;
    private LocalDateTime markedAt;

    public Attendance() {}

    public Attendance(int attendanceId, int sessionId, int studentId,
                      String studentName, String regNumber,
                      Status status, String remarks, LocalDateTime markedAt) {
        this.attendanceId = attendanceId;
        this.sessionId    = sessionId;
        this.studentId    = studentId;
        this.studentName  = studentName;
        this.regNumber    = regNumber;
        this.status       = status;
        this.remarks      = remarks;
        this.markedAt     = markedAt;
    }

    public int           getAttendanceId()                         { return attendanceId; }
    public void          setAttendanceId(int attendanceId)         { this.attendanceId = attendanceId; }

    public int           getSessionId()                            { return sessionId; }
    public void          setSessionId(int sessionId)               { this.sessionId = sessionId; }

    public int           getStudentId()                            { return studentId; }
    public void          setStudentId(int studentId)               { this.studentId = studentId; }

    public String        getStudentName()                          { return studentName; }
    public void          setStudentName(String studentName)        { this.studentName = studentName; }

    public String        getRegNumber()                            { return regNumber; }
    public void          setRegNumber(String regNumber)            { this.regNumber = regNumber; }

    public Status        getStatus()                               { return status; }
    public void          setStatus(Status status)                  { this.status = status; }

    public String        getRemarks()                              { return remarks; }
    public void          setRemarks(String remarks)                { this.remarks = remarks; }

    public LocalDateTime getMarkedAt()                             { return markedAt; }
    public void          setMarkedAt(LocalDateTime markedAt)       { this.markedAt = markedAt; }
}
