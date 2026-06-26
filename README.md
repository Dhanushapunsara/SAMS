# Student Attendance Management System (SAMS)

A Java desktop application for managing student attendance in educational institutions. Built with JavaFX and MySQL as part of an Object-Oriented Programming coursework project.

---

## Project Overview

SAMS allows administrative staff and lecturers to manage courses, students, class schedules, and attendance records. The system enforces role-based access control — admins manage all data while lecturers focus on marking attendance for their classes.

**Two user roles:**
- **Admin** — full access to courses, subjects, students, lecturers, class scheduling, and attendance reports
- **Lecturer** — access to their own classes, attendance marking, and reports

---

## Technologies Used

| Technology | Purpose |
|------------|---------|
| Java 17 | Core programming language |
| JavaFX 21 | Desktop UI framework |
| MySQL 8 | Relational database |
| JDBC | Database connectivity |
| Maven | Build and dependency management |
| IntelliJ IDEA | IDE |

---

## Architecture

The project follows a **Layered (N-Tier) Architecture**:

```
Presentation Layer   →   JavaFX Controllers + FXML screens
Service Layer        →   Business logic and validation
Data Access Layer    →   DAO classes using JDBC
Data Layer           →   MySQL database (sams_db)
```

**Package structure:**
```
src/main/java/com/sams/
├── MainApp.java
├── controller/        ← Presentation layer
├── service/           ← Service layer
├── dao/               ← Data access layer
├── model/             ← Data models
└── util/              ← DBConnection, SceneManager

src/main/resources/
├── fxml/              ← UI screen layouts
└── css/               ← Stylesheet
```

---

## Database Schema

The system uses 8 tables:

| Table | Description |
|-------|-------------|
| `users` | Login credentials and roles |
| `courses` | Available courses |
| `subjects` | Subjects belonging to each course |
| `students` | Student profiles |
| `lecturers` | Lecturer profiles linked to users |
| `lecturer_subjects` | Which subjects each lecturer teaches |
| `class_sessions` | Scheduled class sessions |
| `attendance` | Per-student attendance records |

---

## Setup Instructions

### Prerequisites

Make sure you have the following installed:
- Java JDK 17 or higher — https://adoptium.net
- MySQL 8 — https://dev.mysql.com/downloads/mysql
- MySQL Workbench — https://dev.mysql.com/downloads/workbench
- IntelliJ IDEA (Community or Ultimate) — https://www.jetbrains.com/idea
- Maven (bundled with IntelliJ, no separate install needed)

---

### Step 1 — Set up the database

1. Open **MySQL Workbench** and connect to your local MySQL instance
2. Go to **File → Open SQL Script**
3. Open the file `sql/sams_database.sql` from this project
4. Click the **lightning bolt** button to execute the script
5. Refresh the Schemas panel — you should see `sams_db` with 8 tables

---

### Step 2 — Open the project in IntelliJ

1. Open **IntelliJ IDEA**
2. Click **Open** and select the project root folder (the one containing `pom.xml`)
3. IntelliJ will detect the Maven project automatically
4. Click **Load Maven Changes** when prompted in the top right
5. Wait for dependencies to download (takes 1–2 minutes)

---

### Step 3 — Configure the database password

Open `src/main/java/com/sams/util/DBConnection.java` and update line:

```java
private static final String PASSWORD = "root";
```

Change `"root"` to your actual MySQL root password. If your MySQL has no password, use `""`.

---

### Step 4 — Run the application

1. In IntelliJ, click the **dropdown** next to the green play button → **Edit Configurations**
2. Click **+** → select **Maven**
3. Set **Name** to `Run SAMS` and **Run** to `javafx:run`
4. Click **OK**
5. Select **Run SAMS** from the dropdown and click the green **play** button

The login window will appear.

---

## Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Lecturer | `lec_janaka` | `janaka123` |
| Lecturer | `lec_rashmi` | `rashmi123` |
| Lecturer | `lec_kasun` | `kasun123` |

> All credentials are stored in the `users` table of `sams_db`. You can add more users directly through the Admin panel after logging in.

---

## Features

**Admin can:**
- Add, edit, and delete courses
- Add, edit, and delete subjects (linked to courses)
- Add, edit, and delete student records
- Add, edit, and delete lecturer accounts
- Schedule class sessions
- View attendance reports filtered by student, subject, or date range

**Lecturer can:**
- View their assigned class sessions
- Mark student attendance (Present / Absent / Late) per session
- View attendance reports

---

## Academic Integrity

This project was developed as individual coursework for the Object-Oriented Programming module. External references and libraries used are acknowledged above. AI tools were consulted during development and all generated code was reviewed, understood, and adapted.