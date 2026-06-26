package com.sams.model;

/**
 * MODEL — represents a row in the `lecturers` table joined with `users`.
 */
public class Lecturer {

    private int    lecturerId;
    private int    userId;
    private String employeeId;
    private String department;
    private String fullName;    // from users table
    private String username;    // from users table
    private String password;    // from users table (for creating/editing)

    public Lecturer() {}

    public Lecturer(int lecturerId, int userId, String employeeId,
                    String department, String fullName, String username) {
        this.lecturerId = lecturerId;
        this.userId     = userId;
        this.employeeId = employeeId;
        this.department = department;
        this.fullName   = fullName;
        this.username   = username;
    }

    public int    getLecturerId()                          { return lecturerId; }
    public void   setLecturerId(int lecturerId)            { this.lecturerId = lecturerId; }

    public int    getUserId()                              { return userId; }
    public void   setUserId(int userId)                    { this.userId = userId; }

    public String getEmployeeId()                          { return employeeId; }
    public void   setEmployeeId(String employeeId)         { this.employeeId = employeeId; }

    public String getDepartment()                          { return department; }
    public void   setDepartment(String department)         { this.department = department; }

    public String getFullName()                            { return fullName; }
    public void   setFullName(String fullName)             { this.fullName = fullName; }

    public String getUsername()                            { return username; }
    public void   setUsername(String username)             { this.username = username; }

    public String getPassword()                            { return password; }
    public void   setPassword(String password)             { this.password = password; }

    @Override
    public String toString() { return employeeId + " — " + fullName; }
}
