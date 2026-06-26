package com.sams.controller;

import com.sams.model.*;
import com.sams.service.*;
import com.sams.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * PRESENTATION LAYER — Admin dashboard.
 */
public class AdminDashboardController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label     userLabel;

    private final AuthService         authService     = AuthService.getInstance();
    private final CourseService       courseService   = new CourseService();
    private final StudentService      studentService  = new StudentService();
    private final LecturerService     lecturerService = new LecturerService();
    private final ClassSessionService sessionService  = new ClassSessionService();
    private final AttendanceService   attService      = new AttendanceService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userLabel.setText("Hello, " + authService.getLoggedInUser().getFullName());
        showDashboard();
    }

    //  SIDEBAR NAVIGATION  (no Attendance button for admin)

    @FXML public void showDashboard() { contentArea.getChildren().setAll(buildDashboardPanel()); }
    @FXML public void showCourses()   { contentArea.getChildren().setAll(buildCoursesPanel()); }
    @FXML public void showSubjects()  { contentArea.getChildren().setAll(buildSubjectsPanel()); }
    @FXML public void showStudents()  { contentArea.getChildren().setAll(buildStudentsPanel()); }
    @FXML public void showLecturers() { contentArea.getChildren().setAll(buildLecturersPanel()); }
    @FXML public void showSchedule()  { contentArea.getChildren().setAll(buildSchedulePanel()); }
    @FXML public void showReports()   { contentArea.getChildren().setAll(buildReportsPanel()); }

    @FXML
    public void handleLogout() {
        authService.logout();
        try { SceneManager.switchScene("/fxml/Login.fxml", "Login"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    //  DASHBOARD

    private VBox buildDashboardPanel() {
        VBox root = new VBox(20);
        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Overview of the system");
        sub.getStyleClass().add("page-subtitle");

        HBox stats = new HBox(16);
        try {
            stats.getChildren().addAll(
                    statCard("Courses",   String.valueOf(courseService.getAllCourses().size()),    "#2E86AB"),
                    statCard("Subjects",  String.valueOf(courseService.getAllSubjects().size()),   "#2E86AB"),
                    statCard("Students",  String.valueOf(studentService.getAllStudents().size()),  "#2E86AB"),
                    statCard("Lecturers", String.valueOf(lecturerService.getAllLecturers().size()),"#2E86AB"),
                    statCard("Sessions",  String.valueOf(sessionService.getAllSessions().size()),  "#2E86AB")
            );
        } catch (SQLException e) { e.printStackTrace(); }

        root.getChildren().addAll(title, sub, stats);
        return root;
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card");
        Label num = new Label(value);
        num.getStyleClass().add("stat-number");
        num.setStyle("-fx-text-fill: " + color + ";");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");
        card.getChildren().addAll(num, lbl);
        return card;
    }

    //  COURSES PANEL

    private VBox buildCoursesPanel() {
        VBox root = new VBox(16);
        Label title = new Label("Course Management");
        title.getStyleClass().add("page-title");

        Button addBtn = new Button("+ Add Course");
        addBtn.getStyleClass().add("btn-primary");

        TableView<Course> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Course, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));

        TableColumn<Course, String> colName = new TableColumn<>("Course Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Course, String> colDur = new TableColumn<>("Duration (yrs)");
        colDur.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getDuration())));

        TableColumn<Course, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Course, Void> colActions = actionColumn(table,
                c -> showCourseDialog(c, table),
                c -> { try { if (confirm("Delete this course?")) {
                    courseService.deleteCourse(c.getCourseId());
                    refreshTable(table, courseService.getAllCourses());
                }} catch (Exception e) { alert(e.getMessage()); }}
        );

        table.getColumns().addAll(colCode, colName, colDur, colDesc, colActions);
        try { table.setItems(FXCollections.observableArrayList(courseService.getAllCourses())); }
        catch (SQLException e) { alert(e.getMessage()); }

        addBtn.setOnAction(e -> showCourseDialog(null, table));
        root.getChildren().addAll(title, new HBox(addBtn), table);
        return root;
    }

    private void showCourseDialog(Course existing, TableView<Course> table) {
        boolean isEdit = existing != null;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Course" : "Add Course");
        dialog.setHeaderText(null);

        // ── Fields ────────────────────────────────────────────
        Label lCode = boldLabel("Course Code:");
        TextField fCode = new TextField(isEdit ? existing.getCourseCode() : "");

        Label lName = boldLabel("Course Name:");
        TextField fName = new TextField(isEdit ? existing.getCourseName() : "");

        Label lDur = boldLabel("Duration (years):");
        TextField fDur = new TextField(isEdit ? String.valueOf(existing.getDuration()) : "");

        Label lDesc = boldLabel("Description:");
        TextArea fDesc = new TextArea(isEdit ? existing.getDescription() : "");
        fDesc.setPrefRowCount(3);
        fDesc.setWrapText(true);

        // ── Layout ────────────────────────────────────────────
        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 30, 10, 30));
        content.setPrefWidth(400);
        content.getChildren().addAll(lCode, fCode, lName, fName, lDur, fDur, lDesc, fDesc);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Course c = isEdit ? existing : new Course();
                c.setCourseCode(fCode.getText().trim());
                c.setCourseName(fName.getText().trim());
                try { c.setDuration(Integer.parseInt(fDur.getText().trim())); }
                catch (NumberFormatException ex) { c.setDuration(3); }
                c.setDescription(fDesc.getText().trim());

                if (isEdit) courseService.updateCourse(c);
                else        courseService.addCourse(c);
                refreshTable(table, courseService.getAllCourses());
            } catch (Exception e) { alert(e.getMessage()); }
        }
    }

    //  SUBJECTS PANEL

    private VBox buildSubjectsPanel() {
        VBox root = new VBox(16);
        Label title = new Label("Subject Management");
        title.getStyleClass().add("page-title");

        Button addBtn = new Button("+ Add Subject");
        addBtn.getStyleClass().add("btn-primary");

        TableView<Subject> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Subject, String> c1 = new TableColumn<>("Subject Code");
        c1.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));

        TableColumn<Subject, String> c2 = new TableColumn<>("Subject Name");
        c2.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        TableColumn<Subject, String> c3 = new TableColumn<>("Course");
        c3.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Subject, String> c4 = new TableColumn<>("Credits");
        c4.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCredits())));

        TableColumn<Subject, Void> colActions = actionColumn(table,
                s -> showSubjectDialog(s, table),
                s -> { try { if (confirm("Delete this subject?")) {
                    courseService.deleteSubject(s.getSubjectId());
                    refreshTable(table, courseService.getAllSubjects());
                }} catch (Exception e) { alert(e.getMessage()); }}
        );

        table.getColumns().addAll(c1, c2, c3, c4, colActions);
        try { table.setItems(FXCollections.observableArrayList(courseService.getAllSubjects())); }
        catch (SQLException e) { alert(e.getMessage()); }

        addBtn.setOnAction(e -> showSubjectDialog(null, table));
        root.getChildren().addAll(title, new HBox(addBtn), table);
        return root;
    }

    private void showSubjectDialog(Subject existing, TableView<Subject> table) {
        boolean isEdit = existing != null;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Subject" : "Add Subject");
        dialog.setHeaderText(null);

        // ── Fields ────────────────────────────────────────────
        Label lCode = boldLabel("Subject Code:");
        TextField fCode = new TextField(isEdit ? existing.getSubjectCode() : "");

        Label lName = boldLabel("Subject Name:");
        TextField fName = new TextField(isEdit ? existing.getSubjectName() : "");

        Label lCourse = boldLabel("Course:");
        ComboBox<Course> courseCombo = new ComboBox<>();
        try { courseCombo.setItems(FXCollections.observableArrayList(courseService.getAllCourses())); }
        catch (SQLException e) { alert(e.getMessage()); }
        courseCombo.setPromptText("Select a course");
        courseCombo.setMaxWidth(Double.MAX_VALUE);
        if (isEdit) {
            courseCombo.getItems().stream()
                    .filter(c -> c.getCourseId() == existing.getCourseId())
                    .findFirst().ifPresent(courseCombo::setValue);
        }

        Label lCredits = boldLabel("Credits:");
        TextField fCredits = new TextField(isEdit ? String.valueOf(existing.getCredits()) : "");
        fCredits.setPromptText("");

        // ── Layout ────────────────────────────────────────────
        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 30, 10, 30));
        content.setPrefWidth(400);
        content.getChildren().addAll(lCode, fCode, lName, fName, lCourse, courseCombo, lCredits, fCredits);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (courseCombo.getValue() == null) { alert("Please select a course."); return; }
                Subject s = isEdit ? existing : new Subject();
                s.setSubjectCode(fCode.getText().trim());
                s.setSubjectName(fName.getText().trim());
                s.setCourseId(courseCombo.getValue().getCourseId());
                try { s.setCredits(Integer.parseInt(fCredits.getText().trim())); }
                catch (NumberFormatException ex) { s.setCredits(3); }

                if (isEdit) courseService.updateSubject(s);
                else        courseService.addSubject(s);
                refreshTable(table, courseService.getAllSubjects());
            } catch (Exception e) { alert(e.getMessage()); }
        }
    }

    //  STUDENTS PANEL

    private VBox buildStudentsPanel() {
        VBox root = new VBox(16);
        Label title = new Label("Student Management");
        title.getStyleClass().add("page-title");

        Button addBtn = new Button("+ Add Student");
        addBtn.getStyleClass().add("btn-primary");

        TableView<Student> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Student, String> c1 = new TableColumn<>("Reg No.");
        c1.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        TableColumn<Student, String> c2 = new TableColumn<>("Full Name");
        c2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));
        TableColumn<Student, String> c3 = new TableColumn<>("Email");
        c3.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<Student, String> c4 = new TableColumn<>("Phone");
        c4.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Student, String> c5 = new TableColumn<>("Course");
        c5.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        TableColumn<Student, String> c6 = new TableColumn<>("Enrolled");
        c6.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEnrolledAt().toString()));

        TableColumn<Student, Void> colA = actionColumn(table,
                s -> showStudentDialog(s, table),
                s -> { try { if (confirm("Delete this student?")) {
                    studentService.deleteStudent(s.getStudentId());
                    refreshTable(table, studentService.getAllStudents());
                }} catch (Exception e) { alert(e.getMessage()); }}
        );

        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, colA);
        try { table.setItems(FXCollections.observableArrayList(studentService.getAllStudents())); }
        catch (SQLException e) { alert(e.getMessage()); }

        addBtn.setOnAction(e -> showStudentDialog(null, table));
        root.getChildren().addAll(title, new HBox(addBtn), table);
        return root;
    }

    private void showStudentDialog(Student existing, TableView<Student> table) {
        boolean isEdit = existing != null;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Student" : "Add Student");
        dialog.setHeaderText(null);

        Label lReg  = boldLabel("Registration Number:");
        TextField fReg = new TextField(isEdit ? existing.getRegNumber() : "");

        Label lFn   = boldLabel("First Name:");
        TextField fFn  = new TextField(isEdit ? existing.getFirstName() : "");

        Label lLn   = boldLabel("Last Name:");
        TextField fLn  = new TextField(isEdit ? existing.getLastName() : "");

        Label lEm   = boldLabel("Email:");
        TextField fEm  = new TextField(isEdit ? existing.getEmail() : "");

        Label lPh   = boldLabel("Phone:");
        TextField fPh  = new TextField(isEdit ? existing.getPhone() : "");

        Label lCo   = boldLabel("Course:");
        ComboBox<Course> courseCombo = new ComboBox<>();
        try { courseCombo.setItems(FXCollections.observableArrayList(courseService.getAllCourses())); }
        catch (SQLException e) { alert(e.getMessage()); }
        courseCombo.setPromptText("Select a course");
        courseCombo.setMaxWidth(Double.MAX_VALUE);
        if (isEdit) courseCombo.getItems().stream()
                .filter(c -> c.getCourseId() == existing.getCourseId())
                .findFirst().ifPresent(courseCombo::setValue);

        Label lDt   = boldLabel("Enrolment Date:");
        DatePicker fDt = new DatePicker(isEdit ? existing.getEnrolledAt() : LocalDate.now());
        fDt.setMaxWidth(Double.MAX_VALUE);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 30, 10, 30));
        content.setPrefWidth(420);
        content.getChildren().addAll(lReg, fReg, lFn, fFn, lLn, fLn, lEm, fEm,
                lPh, fPh, lCo, courseCombo, lDt, fDt);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (courseCombo.getValue() == null) { alert("Please select a course."); return; }
                Student s = isEdit ? existing : new Student();
                s.setRegNumber(fReg.getText().trim());
                s.setFirstName(fFn.getText().trim());
                s.setLastName(fLn.getText().trim());
                s.setEmail(fEm.getText().trim());
                s.setPhone(fPh.getText().trim());
                s.setCourseId(courseCombo.getValue().getCourseId());
                s.setEnrolledAt(fDt.getValue());

                if (isEdit) studentService.updateStudent(s);
                else        studentService.addStudent(s);
                refreshTable(table, studentService.getAllStudents());
            } catch (Exception e) { alert(e.getMessage()); }
        }
    }

    //  LECTURERS PANEL

    private VBox buildLecturersPanel() {
        VBox root = new VBox(16);
        Label title = new Label("Lecturer Management");
        title.getStyleClass().add("page-title");

        Button addBtn = new Button("+ Add Lecturer");
        addBtn.getStyleClass().add("btn-primary");

        TableView<Lecturer> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Lecturer, String> c1 = new TableColumn<>("Employee ID");
        c1.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        TableColumn<Lecturer, String> c2 = new TableColumn<>("Full Name");
        c2.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        TableColumn<Lecturer, String> c3 = new TableColumn<>("Username");
        c3.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<Lecturer, String> c4 = new TableColumn<>("Department");
        c4.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<Lecturer, Void> colA = actionColumn(table,
                l -> showLecturerDialog(l, table),
                l -> { try { if (confirm("Delete this lecturer?")) {
                    lecturerService.deleteLecturer(l.getUserId());
                    refreshTable(table, lecturerService.getAllLecturers());
                }} catch (Exception e) { alert(e.getMessage()); }}
        );

        table.getColumns().addAll(c1, c2, c3, c4, colA);
        try { table.setItems(FXCollections.observableArrayList(lecturerService.getAllLecturers())); }
        catch (SQLException e) { alert(e.getMessage()); }

        addBtn.setOnAction(e -> showLecturerDialog(null, table));
        root.getChildren().addAll(title, new HBox(addBtn), table);
        return root;
    }

    private void showLecturerDialog(Lecturer existing, TableView<Lecturer> table) {
        boolean isEdit = existing != null;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Lecturer" : "Add Lecturer");
        dialog.setHeaderText(null);

        Label lEmp  = boldLabel("Employee ID:");
        TextField fEmp  = new TextField(isEdit ? existing.getEmployeeId() : "");

        Label lName = boldLabel("Full Name:");
        TextField fName = new TextField(isEdit ? existing.getFullName() : "");

        Label lUser = boldLabel("Username:");
        TextField fUser = new TextField(isEdit ? existing.getUsername() : "");

        Label lPass = boldLabel("Password:");
        TextField fPass = new TextField(isEdit && existing.getPassword() != null ? existing.getPassword() : "");

        Label lDept = boldLabel("Department:");
        TextField fDept = new TextField(isEdit ? existing.getDepartment() : "");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 30, 10, 30));
        content.setPrefWidth(400);
        content.getChildren().addAll(lEmp, fEmp, lName, fName, lUser, fUser, lPass, fPass, lDept, fDept);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Lecturer l = isEdit ? existing : new Lecturer();
                l.setEmployeeId(fEmp.getText().trim());
                l.setFullName(fName.getText().trim());
                l.setUsername(fUser.getText().trim());
                l.setPassword(fPass.getText().trim());
                l.setDepartment(fDept.getText().trim());

                if (isEdit) lecturerService.updateLecturer(l);
                else        lecturerService.addLecturer(l);
                refreshTable(table, lecturerService.getAllLecturers());
            } catch (Exception e) { alert(e.getMessage()); }
        }
    }

    //  CLASS SCHEDULE PANEL

    private VBox buildSchedulePanel() {
        VBox root = new VBox(16);
        Label title = new Label("Class Schedule");
        title.getStyleClass().add("page-title");

        Button addBtn = new Button("+ Schedule Class");
        addBtn.getStyleClass().add("btn-primary");

        TableView<ClassSession> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<ClassSession, String> c1 = new TableColumn<>("Subject");
        c1.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        TableColumn<ClassSession, String> c2 = new TableColumn<>("Lecturer");
        c2.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));
        TableColumn<ClassSession, String> c3 = new TableColumn<>("Date");
        c3.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSessionDate().toString()));
        TableColumn<ClassSession, String> c4 = new TableColumn<>("Start");
        c4.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStartTime().toString()));
        TableColumn<ClassSession, String> c5 = new TableColumn<>("End");
        c5.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEndTime().toString()));
        TableColumn<ClassSession, String> c6 = new TableColumn<>("Venue");
        c6.setCellValueFactory(new PropertyValueFactory<>("venue"));

        TableColumn<ClassSession, Void> colA = actionColumn(table,
                s -> showSessionDialog(s, table),
                s -> { try { if (confirm("Delete this session?")) {
                    sessionService.deleteSession(s.getSessionId());
                    refreshTable(table, sessionService.getAllSessions());
                }} catch (Exception e) { alert(e.getMessage()); }}
        );

        table.getColumns().addAll(c1, c2, c3, c4, c5, c6, colA);
        try { table.setItems(FXCollections.observableArrayList(sessionService.getAllSessions())); }
        catch (SQLException e) { alert(e.getMessage()); }

        addBtn.setOnAction(e -> showSessionDialog(null, table));
        root.getChildren().addAll(title, new HBox(addBtn), table);
        return root;
    }

    private void showSessionDialog(ClassSession existing, TableView<ClassSession> table) {
        boolean isEdit = existing != null;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Class Session" : "Schedule New Class");
        dialog.setHeaderText(null);

        Label lSub = boldLabel("Subject:");
        ComboBox<Subject> subjectCombo = new ComboBox<>();
        try { subjectCombo.setItems(FXCollections.observableArrayList(courseService.getAllSubjects())); }
        catch (SQLException e) { alert(e.getMessage()); }
        subjectCombo.setPromptText("Select a subject");
        subjectCombo.setMaxWidth(Double.MAX_VALUE);

        Label lLec = boldLabel("Lecturer:");
        ComboBox<Lecturer> lecturerCombo = new ComboBox<>();
        try { lecturerCombo.setItems(FXCollections.observableArrayList(lecturerService.getAllLecturers())); }
        catch (SQLException e) { alert(e.getMessage()); }
        lecturerCombo.setPromptText("Select a lecturer");
        lecturerCombo.setMaxWidth(Double.MAX_VALUE);

        if (isEdit) {
            subjectCombo.getItems().stream()
                    .filter(s -> s.getSubjectId() == existing.getSubjectId())
                    .findFirst().ifPresent(subjectCombo::setValue);
            lecturerCombo.getItems().stream()
                    .filter(l -> l.getLecturerId() == existing.getLecturerId())
                    .findFirst().ifPresent(lecturerCombo::setValue);
        }

        Label lDate  = boldLabel("Session Date:");
        DatePicker fDate = new DatePicker(isEdit ? existing.getSessionDate() : LocalDate.now());
        fDate.setMaxWidth(Double.MAX_VALUE);

        Label lStart = boldLabel("Start Time (HH:mm):");
        TextField fStart = new TextField(isEdit ? existing.getStartTime().toString() : "");

        Label lEnd   = boldLabel("End Time (HH:mm):");
        TextField fEnd = new TextField(isEdit ? existing.getEndTime().toString() : "");

        Label lVenue = boldLabel("Venue:");
        TextField fVenue = new TextField(isEdit ? existing.getVenue() : "");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 30, 10, 30));
        content.setPrefWidth(420);
        content.getChildren().addAll(lSub, subjectCombo, lLec, lecturerCombo,
                lDate, fDate, lStart, fStart, lEnd, fEnd, lVenue, fVenue);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (subjectCombo.getValue()  == null) { alert("Please select a subject.");  return; }
                if (lecturerCombo.getValue() == null) { alert("Please select a lecturer."); return; }
                ClassSession cs = isEdit ? existing : new ClassSession();
                cs.setSubjectId(subjectCombo.getValue().getSubjectId());
                cs.setLecturerId(lecturerCombo.getValue().getLecturerId());
                cs.setSessionDate(fDate.getValue());
                try {
                    cs.setStartTime(java.time.LocalTime.parse(fStart.getText().trim()));
                    cs.setEndTime(java.time.LocalTime.parse(fEnd.getText().trim()));
                } catch (Exception ex) { alert("Invalid time format. Use HH:mm (e.g. 09:00)"); return; }
                cs.setVenue(fVenue.getText().trim());

                if (isEdit) sessionService.updateSession(cs);
                else        sessionService.addSession(cs);
                refreshTable(table, sessionService.getAllSessions());
            } catch (Exception e) { alert(e.getMessage()); }
        }
    }

    //  REPORTS PANEL

    private VBox buildReportsPanel() {
        VBox root = new VBox(16);
        Label title = new Label("Attendance Reports");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Filter attendance records by student, subject, or date range");
        sub.getStyleClass().add("page-subtitle");

        ComboBox<Student> studentCombo = new ComboBox<>();
        ComboBox<Subject> subjectCombo = new ComboBox<>();
        DatePicker fromDP = new DatePicker();
        DatePicker toDP   = new DatePicker();
        Button searchBtn  = new Button("🔍  Search");
        searchBtn.getStyleClass().add("btn-primary");

        try {
            studentCombo.getItems().add(null);
            studentCombo.getItems().addAll(studentService.getAllStudents());
            subjectCombo.getItems().add(null);
            subjectCombo.getItems().addAll(courseService.getAllSubjects());
        } catch (SQLException e) { alert(e.getMessage()); }

        studentCombo.setPromptText("All Students");
        subjectCombo.setPromptText("All Subjects");
        fromDP.setPromptText("From Date");
        toDP.setPromptText("To Date");

        // Filter bar
        HBox filters = new HBox(12);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setStyle("-fx-background-color: white; -fx-padding: 14; -fx-background-radius: 8;");
        filters.getChildren().addAll(
                boldLabel("Student:"), studentCombo,
                boldLabel("Subject:"), subjectCombo,
                boldLabel("From:"), fromDP,
                boldLabel("To:"), toDP,
                searchBtn
        );

        // Results table
        TableView<Attendance> reportTable = new TableView<>();
        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(reportTable, Priority.ALWAYS);

        TableColumn<Attendance, String> c1 = new TableColumn<>("Reg No.");
        c1.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        TableColumn<Attendance, String> c2 = new TableColumn<>("Student Name");
        c2.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        TableColumn<Attendance, String> c3 = new TableColumn<>("Status");
        c3.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));
        TableColumn<Attendance, String> c4 = new TableColumn<>("Session ID");
        c4.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getSessionId())));

        reportTable.getColumns().addAll(c1, c2, c3, c4);

        searchBtn.setOnAction(e -> {
            int stuId = studentCombo.getValue() != null ? studentCombo.getValue().getStudentId() : -1;
            int subId = subjectCombo.getValue() != null ? subjectCombo.getValue().getSubjectId() : -1;
            try {
                List<Attendance> result = attService.getReport(stuId, subId, fromDP.getValue(), toDP.getValue());
                reportTable.setItems(FXCollections.observableArrayList(result));
            } catch (SQLException ex) { alert(ex.getMessage()); }
        });

        root.getChildren().addAll(title, sub, filters, reportTable);
        return root;
    }

    //  HELPERS

    /** Creates a bold label — used as field titles inside dialogs. */
    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        return l;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<T, String> col(String header, String property) {
        TableColumn<T, String> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }

    private <T> TableColumn<T, Void> actionColumn(TableView<T> table,
                                                  java.util.function.Consumer<T> editAction,
                                                  java.util.function.Consumer<T> deleteAction) {
        TableColumn<T, Void> col = new TableColumn<>("Actions");
        col.setMinWidth(150);
        col.setCellFactory(c -> new TableCell<>() {
            final Button editBtn   = new Button("Edit");
            final Button deleteBtn = new Button("Delete");
            final HBox   box       = new HBox(8, editBtn, deleteBtn);
            {
                editBtn.getStyleClass().add("btn-secondary");
                deleteBtn.getStyleClass().add("btn-danger");
                editBtn.setStyle("-fx-font-size:11px; -fx-padding: 4 10;");
                deleteBtn.setStyle("-fx-font-size:11px; -fx-padding: 4 10;");
                editBtn.setOnAction(e   -> editAction.accept(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> deleteAction.accept(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });
        return col;
    }

    private <T> void refreshTable(TableView<T> table, List<T> data) {
        table.setItems(FXCollections.observableArrayList(data));
    }

    private void styleDialog(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets()
                .add(getClass().getResource("/css/styles.css").toExternalForm());
    }

    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.YES;
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}