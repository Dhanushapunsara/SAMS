package com.sams.controller;

import com.sams.model.*;
import com.sams.service.*;
import com.sams.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * PRESENTATION LAYER — Lecturer dashboard.
 */
public class LecturerDashboardController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label     userLabel;

    private final AuthService         authService    = AuthService.getInstance();
    private final LecturerService     lecturerService= new LecturerService();
    private final ClassSessionService sessionService = new ClassSessionService();
    private final AttendanceService   attService     = new AttendanceService();
    private final CourseService       courseService  = new CourseService();

    private Lecturer currentLecturer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userLabel.setText("Hello, " + authService.getLoggedInUser().getFullName());
        try {
            currentLecturer = lecturerService.getLecturerByUserId(authService.getLoggedInUser().getUserId());
        } catch (SQLException e) { e.printStackTrace(); }
        showDashboard();
    }

    @FXML public void showDashboard()  { contentArea.getChildren().setAll(buildDashboard()); }
    @FXML public void showMyClasses()  { contentArea.getChildren().setAll(buildMyClassesPanel()); }
    @FXML public void showAttendance() { contentArea.getChildren().setAll(buildAttendancePanel()); }
    @FXML public void showReports()    { contentArea.getChildren().setAll(buildReportsPanel()); }

    @FXML
    public void handleLogout() {
        authService.logout();
        try { SceneManager.switchScene("/fxml/Login.fxml", "Login"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    // ─── Dashboard ───────────────────────────────────────────
    private VBox buildDashboard() {
        VBox root = new VBox(20);
        Label title = new Label("My Dashboard");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Welcome, " + authService.getLoggedInUser().getFullName());
        sub.getStyleClass().add("page-subtitle");

        int sessionCount = 0;
        try {
            if (currentLecturer != null)
                sessionCount = sessionService.getSessionsByLecturer(currentLecturer.getLecturerId()).size();
        } catch (SQLException e) { e.printStackTrace(); }

        HBox stats = new HBox(16);
        stats.getChildren().add(statCard("My Classes", String.valueOf(sessionCount), "#2E86AB"));
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

    // ─── My Classes ──────────────────────────────────────────
    private VBox buildMyClassesPanel() {
        VBox root = new VBox(16);
        Label title = new Label("My Classes");
        title.getStyleClass().add("page-title");

        TableView<ClassSession> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<ClassSession, String> c1 = new TableColumn<>("Subject");
        c1.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSubjectName()));
        TableColumn<ClassSession, String> c2 = new TableColumn<>("Date");
        c2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSessionDate().toString()));
        TableColumn<ClassSession, String> c3 = new TableColumn<>("Start");
        c3.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStartTime().toString()));
        TableColumn<ClassSession, String> c4 = new TableColumn<>("End");
        c4.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEndTime().toString()));
        TableColumn<ClassSession, String> c5 = new TableColumn<>("Venue");
        c5.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getVenue()));

        table.getColumns().addAll(c1, c2, c3, c4, c5);

        try {
            if (currentLecturer != null) {
                table.setItems(FXCollections.observableArrayList(
                        sessionService.getSessionsByLecturer(currentLecturer.getLecturerId())
                ));
            }
        } catch (SQLException e) { alert(e.getMessage()); }

        root.getChildren().addAll(title, table);
        return root;
    }

    // ─── Attendance Marking ───────────────────────────────────
    private VBox buildAttendancePanel() {
        VBox root = new VBox(16);
        Label title = new Label("Mark Attendance");
        title.getStyleClass().add("page-title");

        ComboBox<ClassSession> sessionCombo = new ComboBox<>();
        try {
            if (currentLecturer != null)
                sessionCombo.setItems(FXCollections.observableArrayList(
                        sessionService.getSessionsByLecturer(currentLecturer.getLecturerId())
                ));
        } catch (SQLException e) { alert(e.getMessage()); }

        Button loadBtn = new Button("Load Students");
        loadBtn.getStyleClass().add("btn-primary");

        HBox selector = new HBox(12, new Label("Session:"), sessionCombo, loadBtn);
        selector.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        TableView<Attendance> attTable = new TableView<>();
        attTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(attTable, Priority.ALWAYS);

        TableColumn<Attendance, String> c1 = new TableColumn<>("Reg No.");
        c1.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRegNumber()));
        TableColumn<Attendance, String> c2 = new TableColumn<>("Student");
        c2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentName()));
        TableColumn<Attendance, String> c3 = new TableColumn<>("Status");
        c3.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));
        c3.setCellFactory(col -> new StatusComboCell());

        attTable.getColumns().addAll(c1, c2, c3);

        Button saveBtn = new Button("✅ Save Attendance");
        saveBtn.getStyleClass().add("btn-success");

        loadBtn.setOnAction(e -> {
            if (sessionCombo.getValue() == null) return;
            try {
                attTable.setItems(FXCollections.observableArrayList(
                        attService.getAttendanceSheet(sessionCombo.getValue().getSessionId())
                ));
            } catch (SQLException ex) { alert(ex.getMessage()); }
        });

        saveBtn.setOnAction(e -> {
            try {
                attService.saveAll(attTable.getItems());
                showInfo("Attendance saved!");
            } catch (SQLException ex) { alert(ex.getMessage()); }
        });

        root.getChildren().addAll(title, selector, attTable, saveBtn);
        return root;
    }

    // ─── Reports ─────────────────────────────────────────────
    private VBox buildReportsPanel() {
        VBox root = new VBox(16);
        Label title = new Label("Attendance Reports");
        title.getStyleClass().add("page-title");

        ComboBox<Subject> subjectCombo = new ComboBox<>();
        javafx.scene.control.DatePicker fromDP = new javafx.scene.control.DatePicker();
        javafx.scene.control.DatePicker toDP   = new javafx.scene.control.DatePicker();
        Button searchBtn = new Button("🔍 Search");
        searchBtn.getStyleClass().add("btn-primary");

        try {
            subjectCombo.getItems().add(null);
            subjectCombo.getItems().addAll(courseService.getAllSubjects());
        } catch (SQLException e) { alert(e.getMessage()); }
        subjectCombo.setPromptText("All Subjects");

        HBox filters = new HBox(10, new Label("Subject:"), subjectCombo,
                new Label("From:"), fromDP, new Label("To:"), toDP, searchBtn);
        filters.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        filters.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 8;");

        TableView<Attendance> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Attendance, String> c1 = new TableColumn<>("Reg No.");
        c1.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRegNumber()));
        TableColumn<Attendance, String> c2 = new TableColumn<>("Student");
        c2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentName()));
        TableColumn<Attendance, String> c3 = new TableColumn<>("Status");
        c3.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));

        table.getColumns().addAll(c1, c2, c3);

        searchBtn.setOnAction(e -> {
            int subId = subjectCombo.getValue() != null ? subjectCombo.getValue().getSubjectId() : -1;
            try {
                table.setItems(FXCollections.observableArrayList(
                        attService.getReport(-1, subId, fromDP.getValue(), toDP.getValue())
                ));
            } catch (SQLException ex) { alert(ex.getMessage()); }
        });

        root.getChildren().addAll(title, filters, table);
        return root;
    }

    private void alert(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
    private void showInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }

    private static class StatusComboCell extends TableCell<Attendance, String> {
        private final ComboBox<String> combo = new ComboBox<>(
                FXCollections.observableArrayList("PRESENT", "ABSENT", "LATE")
        );
        {
            combo.setOnAction(e -> {
                Attendance a = getTableRow().getItem();
                if (a != null) a.setStatus(Attendance.Status.valueOf(combo.getValue()));
            });
        }
        @Override protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) { setGraphic(null); return; }
            combo.setValue(item);
            setGraphic(combo);
        }
    }
}