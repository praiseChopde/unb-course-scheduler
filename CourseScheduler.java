import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/**
 * CourseScheduler - JavaFX application that ties together all OOP concepts.
 *
 *
 *   INHERITANCE     : Person -> Student, Person -> Advisor -> RegularAdvisor/HeadAdvisor
 *   POLYMORPHISM    : Person references hold Student/Advisor objects; displayInfo() behaves
 *                     differently for each type at runtime
 *   ENCAPSULATION   : all fields private; accessed through public methods only
 *   ABSTRACT CLASSES: Person and Advisor cannot be instantiated directly
 *   EXCEPTION HANDLING: NumberFormatException (bad input), IOException (file write)
 *   FILE I/O        : PrintWriter appends every action to schedule_log.txt
 *   COLLECTIONS     : ArrayList<Student>, ArrayList<Course>, Stack<String>
 *   RECURSION       : recursive student search mirrors the textbook sum(n) pattern
 *
 * Mapping from LibrarySystem.java:
 *   ArrayList<Member>     -> ArrayList<Student>
 *   ArrayList<Book>       -> ArrayList<Course>
 *   HeadLibrarian admin   -> HeadAdvisor admin
 *   Member section        -> Student section
 *   Book Catalog section  -> Course Catalog section
 *   Borrow / Return       -> Enroll / Drop
 *   Fine Waiver           -> Prerequisite Override
 *   "library_log.txt"     -> "schedule_log.txt"
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version April 2026
 */
public class CourseScheduler extends Application
{
    // =========================================================================
    // DATA STORES
    // =========================================================================

    private ArrayList<Student> students   = new ArrayList<Student>();
    private ArrayList<Course>  catalog    = new ArrayList<Course>();
    private Stack<String>      activityLog = new Stack<String>();

    private HeadAdvisor admin = new HeadAdvisor(
            "Praise Chopde", 9001, "pchopde@unb.ca", 5069001, "ADV-01", "Full Day");

    private static final String LOG_FILE = "schedule_log.txt";

    // =========================================================================
    // GUI FIELDS
    // =========================================================================

    private TextField studentNameField, studentIdField, studentEmailField;
    private TextField studentPhoneField, studentProgramField;
    private TextField searchField;

    private TextField courseCodeField, courseNameField, courseInstructorField;
    private TextField courseCreditsField, courseSemesterField, coursePrereqField;

    private TextField enrollStudentIdField, enrollCourseCodeField;
    private TextField dropStudentIdField,   dropCourseCodeField;

    private ComboBox<String> reportTypeBox;
    private TextField overrideStudentIdField, overrideCourseCodeField;

    // Toast panel — replaces TextArea
    private VBox   toastPanel;       // scrollable list of toast cards
    private ScrollPane toastScroll;  // wraps toastPanel

    // Sidebar nav buttons so we can highlight the active one
    private Button[] navButtons;
    private VBox     centerContent;   // swapped out when nav tab changes

    // Accent colour used throughout
    private static final String ACCENT  = "#1a237e";   // deep UNB blue
    private static final String ACCENT2 = "#283593";   // slightly lighter
    private static final String LIGHT_BG = "#f0f2f8";  // page background
    private static final String CARD_BG  = "#ffffff";  // card / section background
    private static final String BORDER   = "#c5cae9";  // subtle card border

    // =========================================================================
    // start() — entry point
    // =========================================================================

    public void start(Stage primaryStage)
    {
        primaryStage.setTitle(
            "UNB Course Scheduler  —  CS1083 Project  |  Praise Chopde");

        // ── Toast panel (replaces TextArea) ───────────────────────────────
        toastPanel = new VBox(8);
        toastPanel.setPadding(new Insets(8, 12, 8, 12));
        toastScroll = new ScrollPane(toastPanel);
        toastScroll.setFitToWidth(true);
        toastScroll.setPrefHeight(190);
        toastScroll.setStyle("-fx-background-color: #e8eaf6; -fx-background: #e8eaf6;");

        preloadSampleData();

        // ── Top nav bar (BorderPane top region) ───────────────────────────
        HBox topBar = buildTopBar();

        // ── Left sidebar ──────────────────────────────────────────────────
        VBox sidebar = buildSidebar();

        // ── Center: start on Students tab ─────────────────────────────────
        centerContent = new VBox(12);
        centerContent.setPadding(new Insets(16));
        showStudentPanel();     // default view

        ScrollPane centerScroll = new ScrollPane(centerContent);
        centerScroll.setFitToWidth(true);
        centerScroll.setStyle("-fx-background-color: " + LIGHT_BG + ";");

        // ── Output strip at bottom ─────────────────────────────────────────
        Label outLabel = new Label("Notifications");
        outLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #5c6bc0; -fx-padding: 6 0 2 12;");
        VBox outputStrip = new VBox(0, outLabel, toastScroll);
        outputStrip.setStyle("-fx-background-color: #e8eaf6;");

        // ── Root BorderPane — this is the key layout from the snippet ──────
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sidebar);
        root.setCenter(centerScroll);
        root.setBottom(outputStrip);
        root.setStyle("-fx-background-color: " + LIGHT_BG + ";");

        Scene scene = new Scene(root, 960, 780);
        primaryStage.setScene(scene);
        primaryStage.show();

        // ── Width-listener from the responsive layout snippet ──────────────
        // Adjusts top bar spacing as the window resizes
        scene.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            topBar.setSpacing(newWidth.intValue() / 20);
        });
    }

    // =========================================================================
    // TOP BAR
    // =========================================================================

    private HBox buildTopBar()
    {
        Label appTitle = new Label("UNB Course Scheduler");
        appTitle.setStyle(
            "-fx-font-size: 17; -fx-font-weight: bold;" +
            "-fx-text-fill: white; -fx-font-family: 'Segoe UI', Arial;");

        Label subTitle = new Label("CS1083  |  Praise Chopde  |  ID: 3779552");
        subTitle.setStyle("-fx-font-size: 11; -fx-text-fill: #c5cae9;");

        VBox titleBox = new VBox(2, appTitle, subTitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label adminBadge = new Label("Admin: Praise Chopde");
        adminBadge.setStyle(
            "-fx-background-color: #283593; -fx-text-fill: #e8eaf6;" +
            "-fx-padding: 4 12 4 12; -fx-background-radius: 20;" +
            "-fx-font-size: 11;");

        Button unbBtn = new Button("Check UNB Courses");
        unbBtn.setStyle(
            "-fx-background-color: #c62828; -fx-text-fill: white;" +
            "-fx-padding: 5 14 5 14; -fx-background-radius: 20;" +
            "-fx-font-size: 11; -fx-cursor: hand;");
        unbBtn.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(
                    "https://www.unb.ca/academics/calendar/undergraduate/current/" +
                    "frederictoncourses/computer-science/index.html"));
            } catch (Exception ex) {
                showToast("error", "Could not open browser", ex.getMessage());
            }
        });

        HBox topBar = new HBox(16, titleBox, spacer, unbBtn, adminBadge);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 20, 12, 20));
        topBar.setStyle("-fx-background-color: " + ACCENT + ";");
        topBar.setSpacing(16);

        return topBar;
    }

    // =========================================================================
    // LEFT SIDEBAR
    // =========================================================================

    private VBox buildSidebar()
    {
        String[] labels = {
            "👤  Students",
            "📚  Courses",
            "✏️  Enroll / Drop",
            "🔒  Admin Panel",
            "🔧  Utilities"
        };
        Runnable[] actions = {
            this::showStudentPanel,
            this::showCoursePanel,
            this::showEnrollPanel,
            this::showAdminPanel,
            this::showUtilityPanel
        };

        navButtons = new Button[labels.length];
        VBox nav = new VBox(4);
        nav.setPadding(new Insets(16, 8, 16, 8));

        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            Button btn = new Button(labels[i]);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPrefHeight(42);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setStyle(navBtnStyle(false));
            btn.setOnAction(e -> {
                highlightNav(idx);
                actions[idx].run();
            });
            navButtons[i] = btn;
            nav.getChildren().add(btn);
        }

        highlightNav(0);   // Students active by default

        VBox sidebar = new VBox(nav);
        sidebar.setPrefWidth(170);
        sidebar.setStyle(
            "-fx-background-color: #1a237e;" +
            "-fx-border-color: #283593; -fx-border-width: 0 1 0 0;");
        VBox.setVgrow(nav, Priority.ALWAYS);

        return sidebar;
    }

    private String navBtnStyle(boolean active)
    {
        if (active)
            return "-fx-background-color: #3949ab; -fx-text-fill: white;" +
                   "-fx-font-size: 12; -fx-background-radius: 6;" +
                   "-fx-padding: 0 12 0 12; -fx-font-family: 'Segoe UI', Arial;" +
                   "-fx-cursor: hand;";
        else
            return "-fx-background-color: transparent; -fx-text-fill: #c5cae9;" +
                   "-fx-font-size: 12; -fx-background-radius: 6;" +
                   "-fx-padding: 0 12 0 12; -fx-font-family: 'Segoe UI', Arial;" +
                   "-fx-cursor: hand;";
    }

    private void highlightNav(int active)
    {
        for (int i = 0; i < navButtons.length; i++)
            navButtons[i].setStyle(navBtnStyle(i == active));
    }

    // =========================================================================
    // PANEL SWITCHERS  (swap center content)
    // =========================================================================

    private void showStudentPanel()
    {
        centerContent.getChildren().setAll(
            sectionHeader("Student Management",
                "Register students, view the roster, and search by name."),
            buildStudentCard());
    }

    private void showCoursePanel()
    {
        centerContent.getChildren().setAll(
            sectionHeader("Course Catalog",
                "Add new courses and view all available offerings."),
            buildCourseCard());
    }

    private void showEnrollPanel()
    {
        centerContent.getChildren().setAll(
            sectionHeader("Enroll / Drop",
                "Link students to courses or remove them from their plan."),
            buildEnrollCard(),
            buildDropCard());
    }

    private void showAdminPanel()
    {
        centerContent.getChildren().setAll(
            sectionHeader("Admin Panel",
                "HeadAdvisor-only: reports, prerequisite overrides, and system info."),
            buildAdminCard());
    }

    private void showUtilityPanel()
    {
        centerContent.getChildren().setAll(
            sectionHeader("Utilities / Polymorphism Demo",
                "Demonstrate runtime polymorphism and view the activity log."),
            buildUtilityCard());
    }

    // =========================================================================
    // SECTION HEADER
    // =========================================================================

    private VBox sectionHeader(String title, String subtitle)
    {
        Label h = new Label(title);
        h.setStyle("-fx-font-size: 18; -fx-font-weight: bold;" +
                   "-fx-text-fill: " + ACCENT + "; -fx-font-family: 'Segoe UI', Arial;");
        Label s = new Label(subtitle);
        s.setStyle("-fx-font-size: 12; -fx-text-fill: #5c6bc0;");
        VBox box = new VBox(2, h, s);
        box.setPadding(new Insets(0, 0, 4, 0));
        return box;
    }

    // =========================================================================
    // CARD BUILDER HELPERS
    // =========================================================================

    /** Wraps a GridPane in a styled white card. */
    private VBox card(String heading, GridPane grid)
    {
        Label lbl = new Label(heading);
        lbl.setStyle(
            "-fx-font-size: 13; -fx-font-weight: bold;" +
            "-fx-text-fill: " + ACCENT2 + "; -fx-padding: 0 0 6 0;");
        VBox card = new VBox(6, lbl, grid);
        card.setPadding(new Insets(14));
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-border-width: 1;");
        return card;
    }

    // =========================================================================
    // STUDENT CARD
    // =========================================================================

    private VBox buildStudentCard()
    {
        studentNameField    = styledField("Full name");
        studentIdField      = styledField("Student ID (number)");
        studentEmailField   = styledField("Email address");
        studentPhoneField   = styledField("Phone number");
        studentProgramField = styledField("Program (e.g. BCS)");
        searchField         = styledField("Name to search");

        Button createBtn    = accentBtn("Register Student");
        Button viewAllBtn   = outlineBtn("View All Students");
        Button searchBtn    = outlineBtn("Search (Linear)");
        Button recursiveBtn = outlineBtn("Search (Recursive)");

        createBtn.setOnAction(this::createStudent);
        viewAllBtn.setOnAction(this::viewAllStudents);
        searchBtn.setOnAction(this::searchStudentByName);
        recursiveBtn.setOnAction(this::searchStudentRecursive);

        GridPane grid = makeGrid();
        grid.add(fieldLabel("Name:"),    0, 0); grid.add(studentNameField,    1, 0);
        grid.add(fieldLabel("ID:"),      0, 1); grid.add(studentIdField,      1, 1);
        grid.add(fieldLabel("Email:"),   0, 2); grid.add(studentEmailField,   1, 2);
        grid.add(fieldLabel("Phone:"),   0, 3); grid.add(studentPhoneField,   1, 3);
        grid.add(fieldLabel("Program:"), 0, 4); grid.add(studentProgramField, 1, 4);

        HBox createRow = new HBox(8, createBtn, viewAllBtn);
        grid.add(createRow, 1, 5);

        grid.add(fieldLabel("Search:"), 0, 6); grid.add(searchField, 1, 6);
        HBox searchRow = new HBox(8, searchBtn, recursiveBtn);
        grid.add(searchRow, 1, 7);

        return card("Register & Search Students", grid);
    }

    // =========================================================================
    // COURSE CARD
    // =========================================================================

    private VBox buildCourseCard()
    {
        courseCodeField       = styledField("Course code (e.g. CS2333)");
        courseNameField       = styledField("Course name");
        courseInstructorField = styledField("Instructor name");
        courseCreditsField    = styledField("Credit hours (e.g. 3)");
        courseSemesterField   = styledField("Semester: Fall, Winter, or Both");
        coursePrereqField     = styledField("Prereq codes, comma-separated (or leave blank)");

        Button addCourseBtn = accentBtn("Add Course to Catalog");
        Button viewCatalog  = outlineBtn("View Full Catalog");

        addCourseBtn.setOnAction(this::addCourse);
        viewCatalog.setOnAction(this::viewCatalog);

        GridPane grid = makeGrid();
        grid.add(fieldLabel("Code:"),       0, 0); grid.add(courseCodeField,       1, 0);
        grid.add(fieldLabel("Name:"),       0, 1); grid.add(courseNameField,       1, 1);
        grid.add(fieldLabel("Instructor:"), 0, 2); grid.add(courseInstructorField, 1, 2);
        grid.add(fieldLabel("Credits:"),    0, 3); grid.add(courseCreditsField,    1, 3);
        grid.add(fieldLabel("Semester:"),   0, 4); grid.add(courseSemesterField,   1, 4);
        grid.add(fieldLabel("Prereqs:"),    0, 5); grid.add(coursePrereqField,     1, 5);

        HBox btnRow = new HBox(8, addCourseBtn, viewCatalog);
        grid.add(btnRow, 1, 6);

        return card("Add Course to Catalog", grid);
    }

    // =========================================================================
    // ENROLL CARD
    // =========================================================================

    private VBox buildEnrollCard()
    {
        enrollStudentIdField  = styledField("Student ID");
        enrollCourseCodeField = styledField("Course Code");

        Button enrollBtn = accentBtn("Enroll in Course");
        enrollBtn.setOnAction(this::enrollStudentInCourse);

        GridPane grid = makeGrid();
        grid.add(fieldLabel("Student ID:"),  0, 0); grid.add(enrollStudentIdField,  1, 0);
        grid.add(fieldLabel("Course Code:"), 0, 1); grid.add(enrollCourseCodeField, 1, 1);
        grid.add(enrollBtn, 1, 2);

        return card("Enroll Student", grid);
    }

    private VBox buildDropCard()
    {
        dropStudentIdField  = styledField("Student ID");
        dropCourseCodeField = styledField("Course Code");

        Button dropBtn = new Button("Drop Course");
        dropBtn.setStyle(
            "-fx-background-color: #c62828; -fx-text-fill: white;" +
            "-fx-padding: 7 18 7 18; -fx-background-radius: 6;" +
            "-fx-cursor: hand; -fx-font-size: 12;");
        dropBtn.setOnAction(this::dropStudentFromCourse);

        GridPane grid = makeGrid();
        grid.add(fieldLabel("Student ID:"),  0, 0); grid.add(dropStudentIdField,  1, 0);
        grid.add(fieldLabel("Course Code:"), 0, 1); grid.add(dropCourseCodeField, 1, 1);
        grid.add(dropBtn, 1, 2);

        return card("Drop Course", grid);
    }

    // =========================================================================
    // ADMIN CARD
    // =========================================================================

    private VBox buildAdminCard()
    {
        reportTypeBox = new ComboBox<>();
        reportTypeBox.getItems().addAll("Students", "Catalog", "Full");
        reportTypeBox.setValue("Full");
        reportTypeBox.setStyle("-fx-font-size: 12;");

        overrideStudentIdField  = styledField("Student ID");
        overrideCourseCodeField = styledField("Course Code");

        Button reportBtn    = accentBtn("Generate Report");
        Button overrideBtn  = accentBtn("Approve Prereq Override");
        Button showAdminBtn = outlineBtn("Show Admin Info");

        reportBtn.setOnAction(this::generateReport);
        overrideBtn.setOnAction(this::approvePrereqOverride);
        showAdminBtn.setOnAction(this::showAdminInfo);

        Label adminLabel = new Label(
            "Admin: " + admin.getName() + " (" + admin.getRole() + ")");
        adminLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #333; -fx-font-size: 12;");

        GridPane grid = makeGrid();
        grid.add(adminLabel, 0, 0, 2, 1);
        grid.add(fieldLabel("Report Type:"), 0, 1); grid.add(reportTypeBox, 1, 1);

        HBox reportRow = new HBox(8, reportBtn, showAdminBtn);
        grid.add(reportRow, 1, 2);

        grid.add(fieldLabel("Override — Student ID:"),   0, 3); grid.add(overrideStudentIdField,  1, 3);
        grid.add(fieldLabel("Override — Course Code:"),  0, 4); grid.add(overrideCourseCodeField, 1, 4);
        grid.add(overrideBtn, 1, 5);

        return card("Admin Panel  (HeadAdvisor Only)", grid);
    }

    // =========================================================================
    // UTILITY CARD
    // =========================================================================

    private VBox buildUtilityCard()
    {
        Button studentSampleBtn = outlineBtn("Show Sample Student");
        Button advisorSampleBtn = outlineBtn("Show Sample Advisor");
        Button activityBtn      = outlineBtn("Show Recent Activity");
        Button clearBtn         = new Button("Clear Output");
        clearBtn.setStyle(
            "-fx-background-color: #757575; -fx-text-fill: white;" +
            "-fx-padding: 7 18 7 18; -fx-background-radius: 6;" +
            "-fx-cursor: hand; -fx-font-size: 12;");

        studentSampleBtn.setOnAction(this::showSampleStudent);
        advisorSampleBtn.setOnAction(this::showSampleAdvisor);
        activityBtn.setOnAction(this::showRecentActivity);
        clearBtn.setOnAction(e -> toastPanel.getChildren().clear());

        FlowPane fp = new FlowPane(8, 8,
                studentSampleBtn, advisorSampleBtn, activityBtn, clearBtn);
        fp.setPadding(new Insets(4));

        VBox card = new VBox(6,
            new Label("Click any button to demonstrate OOP concepts:"),
            fp);
        card.setPadding(new Insets(14));
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-border-width: 1;");
        return card;
    }

    // =========================================================================
    // STYLE HELPERS
    // =========================================================================

    private TextField styledField(String prompt)
    {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-font-size: 12; -fx-background-radius: 5;" +
            "-fx-border-color: " + BORDER + "; -fx-border-radius: 5;");
        return tf;
    }

    private Label fieldLabel(String text)
    {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12; -fx-text-fill: #37474f;");
        l.setMinWidth(160);
        return l;
    }

    private Button accentBtn(String text)
    {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;" +
            "-fx-padding: 7 18 7 18; -fx-background-radius: 6;" +
            "-fx-cursor: hand; -fx-font-size: 12;");
        return b;
    }

    private Button outlineBtn(String text)
    {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + ACCENT + "; -fx-text-fill: " + ACCENT + ";" +
            "-fx-padding: 6 16 6 16; -fx-background-radius: 6;" +
            "-fx-border-radius: 6; -fx-cursor: hand; -fx-font-size: 12;");
        return b;
    }

    /**
     * Replaces outputArea.setText() throughout the app.
     * Adds a styled toast card to the notification panel at the bottom.
     *
     * @param type    "success", "error", "info", or "warn"
     * @param title   Short headline (shown bold)
     * @param message Full detail text (shown below title in monospace)
     */
    private void showToast(String type, String title, String message)
    {
        // ── Colour scheme per type ─────────────────────────────────────────
        String accentColor, bgColor, dotColor, titleColor;
        switch (type)
        {
            case "success":
                accentColor = "#3B6D11"; bgColor = "#f4f9ee";
                dotColor    = "#639922"; titleColor = "#3B6D11"; break;
            case "error":
                accentColor = "#A32D2D"; bgColor = "#fff5f5";
                dotColor    = "#E24B4A"; titleColor = "#A32D2D"; break;
            case "warn":
                accentColor = "#854F0B"; bgColor = "#fffbf0";
                dotColor    = "#EF9F27"; titleColor = "#854F0B"; break;
            default: // "info"
                accentColor = "#185FA5"; bgColor = "#f0f6fd";
                dotColor    = "#378ADD"; titleColor = "#185FA5"; break;
        }

        // ── Dot indicator ──────────────────────────────────────────────────
        javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(5);
        dot.setFill(javafx.scene.paint.Color.web(dotColor));

        // ── Title label ────────────────────────────────────────────────────
        Label titleLbl = new Label(title);
        titleLbl.setStyle(
            "-fx-font-size: 12; -fx-font-weight: bold;" +
            "-fx-text-fill: " + titleColor + ";");

        // ── Message label (monospace, wraps) ───────────────────────────────
        Label msgLbl = new Label(message);
        msgLbl.setWrapText(true);
        msgLbl.setStyle(
            "-fx-font-family: 'Courier New'; -fx-font-size: 11;" +
            "-fx-text-fill: #444;");

        VBox textBox = new VBox(3, titleLbl, msgLbl);

        HBox toast = new HBox(10, dot, textBox);
        toast.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        toast.setPadding(new Insets(10, 14, 10, 14));
        toast.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-border-color: " + accentColor + ";" +
            "-fx-border-width: 0 0 0 3;" +
            "-fx-border-radius: 0 6 6 0;" +
            "-fx-background-radius: 0 6 6 0;");
        HBox.setHgrow(textBox, Priority.ALWAYS);

        // Insert newest toast at the TOP so latest is always visible
        toastPanel.getChildren().add(0, toast);

        // Auto-scroll to top so the new toast is visible
        toastScroll.setVvalue(0);
    }

    // =========================================================================
    // SHARED GRID FACTORY 
    // =========================================================================

    private GridPane makeGrid()
    {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(4, 0, 4, 0));

        ColumnConstraints col0 = new ColumnConstraints(160);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col0, col1);

        return grid;
    }

    // =========================================================================
    // PRE-LOAD  (untouched)
    // =========================================================================

    private void preloadSampleData()
    {
        Course cs1073  = new Course("CS1073",  "Intro to Computer Science",          "TBA", 3, "Fall");
        Course cs1083  = new Course("CS1083",  "Data Structures",                    "TBA", 3, "Winter");
        cs1083.addPrerequisite("CS1073");

        Course math1823 = new Course("MATH1823", "Calculus I",  "TBA", 3, "Fall");
        Course math1833 = new Course("MATH1833", "Calculus II", "TBA", 3, "Winter");
        math1833.addPrerequisite("MATH1823");

        Course cs2043 = new Course("CS2043", "Software Engineering I",            "TBA", 3, "Fall");
        cs2043.addPrerequisite("CS1083");

        Course cs2263 = new Course("CS2263", "Systems Programming",               "TBA", 3, "Winter");
        cs2263.addPrerequisite("CS1083");

        Course cs2333 = new Course("CS2333", "Computability and Formal Languages", "TBA", 3, "Winter");
        cs2333.addPrerequisite("CS1083");
        cs2333.addPrerequisite("MATH1823");

        Course cs3613 = new Course("CS3613", "Programming Languages",             "TBA", 3, "Both");
        cs3613.addPrerequisite("CS2043");

        Course cs4815 = new Course("CS4815", "Machine Learning",                  "TBA", 3, "Fall");
        cs4815.addPrerequisite("CS2333");

        catalog.add(cs1073); catalog.add(cs1083);
        catalog.add(math1823); catalog.add(math1833);
        catalog.add(cs2043); catalog.add(cs2263);
        catalog.add(cs2333); catalog.add(cs3613); catalog.add(cs4815);

        writeToFile("System started. " + catalog.size() +
                    " UNB CS courses pre-loaded into catalog.");
    }

    // =========================================================================
    // HELPER: writeToFile  (untouched)
    // =========================================================================

    private void writeToFile(String message)
    {
        try
        {
            PrintWriter outFile = new PrintWriter(new java.io.FileWriter(LOG_FILE, true));
            outFile.println(message);
            outFile.println("----------------------------------------");
            outFile.close();
        }
        catch (IOException e)
        {
            showToast("warn", "File write warning", "Could not write to " + LOG_FILE + " — " + e.getMessage() + ". Operation completed in memory.");
        }
    }

    // =========================================================================
    // HELPER: lookups  (untouched)
    // =========================================================================

    private Student findStudentById(int id)
    {
        for (Student s : students)
            if (s.getId() == id) return s;
        return null;
    }

    private Course findCourseByCode(String code)
    {
        for (Course c : catalog)
            if (c.getCode().equalsIgnoreCase(code)) return c;
        return null;
    }

    // =========================================================================
    // EVENT HANDLERS - Student section  (ALL UNTOUCHED)
    // =========================================================================

    public void createStudent(ActionEvent e)
    {
        String name    = studentNameField.getText().trim();
        String idStr   = studentIdField.getText().trim();
        String email   = studentEmailField.getText().trim();
        String phone   = studentPhoneField.getText().trim();
        String program = studentProgramField.getText().trim();

        if (name.isEmpty() || idStr.isEmpty() || email.isEmpty()
                || phone.isEmpty() || program.isEmpty())
        {
            showToast("error", "Missing fields", "All student fields must be filled in: Name, ID, Email, Phone, and Program.");
            return;
        }

        int id, phoneNum;

        try { id = Integer.parseInt(idStr); }
        catch (NumberFormatException ex)
        {
            showToast("error", "Invalid student ID", "ID must be a whole number. You entered: \"" + idStr + "\" — use digits only.");
            return;
        }

        if (findStudentById(id) != null)
        {
            showToast("error", "Duplicate student ID", "A student with ID " + id + " already exists. Use a unique ID.");
            return;
        }

        try
        {
            long phoneLong = Long.parseLong(phone);
            phoneNum = (int) phoneLong;
        }
        catch (NumberFormatException ex)
        {
            showToast("error", "Invalid phone number", "Phone must contain digits only. You entered: \"" + phone + "\"");
            return;
        }

        Student newStudent = new Student(name, id, email, phoneNum, program);
        students.add(newStudent);

        String message = "=== STUDENT REGISTERED ===\n\n" +
                         newStudent.displayInfo()           +
                         "\n\nRole: " + newStudent.getRole()  +
                         "\nTotal students registered: " + students.size();

        showToast("success", "Student registered", message);
        writeToFile("NEW STUDENT\n" + message);
        activityLog.push("Registered student: " + name + " (ID: " + id + ")");

        studentNameField.clear(); studentIdField.clear();
        studentEmailField.clear(); studentPhoneField.clear();
        studentProgramField.clear();
    }

    public void viewAllStudents(ActionEvent e)
    {
        if (students.isEmpty())
        {
            showToast("info", "No students yet", "Use the Student Management section to register students.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== ALL REGISTERED STUDENTS ===\n\n");

        for (int i = 0; i < students.size(); i++)
        {
            sb.append("Student #").append(i + 1).append(":\n");
            sb.append(students.get(i).displayInfo());
            sb.append("\n\n").append("─".repeat(46)).append("\n\n");
        }

        sb.append("Total: ").append(students.size()).append(" student(s)");
        showToast("info", "All registered students", sb.toString());
        writeToFile("=== VIEW ALL STUDENTS ===\n" + sb.toString());
        activityLog.push("Viewed all students (" + students.size() + " total)");
    }

    public void searchStudentByName(ActionEvent e)
    {
        String searchName = searchField.getText().trim();

        if (searchName.isEmpty())
        { showToast("error", "No name entered", "Please enter a name in the Search field."); return; }

        if (students.isEmpty())
        { showToast("info", "No students yet", "Register some students first."); return; }

        Student found = null;
        for (Student s : students)
            if (s.getName().equalsIgnoreCase(searchName)) { found = s; break; }

        String result;
        if (found != null)
            result = "=== STUDENT FOUND  (Linear Search) ===\n\n" +
                     found.displayInfo() + "\n\nRole: " + found.getRole();
        else
            result = "Student \"" + searchName + "\" not found.\n" +
                     "(Linear search checked all " + students.size() + " students.)";

        showToast(found != null ? "success" : "info", found != null ? "Student found (linear)" : "Not found", result);
        writeToFile("LINEAR SEARCH for: " + searchName + "\n" + result);
        activityLog.push("Linear search: \"" + searchName + "\" " +
                         (found != null ? "(found)" : "(not found)"));
        searchField.clear();
    }

    public void searchStudentRecursive(ActionEvent e)
    {
        String searchName = searchField.getText().trim();

        if (searchName.isEmpty())
        { showToast("error", "No name entered", "Please enter a name in the Search field."); return; }

        if (students.isEmpty())
        { showToast("info", "No students yet", "Register some students first."); return; }

        int foundIndex = findStudentRecursive(searchName, students.size() - 1);

        String result;
        if (foundIndex >= 0)
            result = "=== STUDENT FOUND  (Recursive Search) ===\n\n" +
                     students.get(foundIndex).displayInfo()           +
                     "\n\nFound at position: " + (foundIndex + 1)    +
                     "\nRole: " + students.get(foundIndex).getRole()  +
                     "\n\n(Result found using RECURSION)";
        else
            result = "Student \"" + searchName + "\" not found.\n" +
                     "(Recursive search checked all " + students.size() + " students.)";

        showToast(foundIndex >= 0 ? "success" : "info", foundIndex >= 0 ? "Student found (recursive)" : "Not found", result);
        writeToFile("RECURSIVE SEARCH for: " + searchName + "\n" + result);
        activityLog.push("Recursive search: \"" + searchName + "\" " +
                         (foundIndex >= 0 ? "(found)" : "(not found)"));
        searchField.clear();
    }

    private int findStudentRecursive(String name, int index)
    {
        if (index < 0) return -1;
        if (students.get(index).getName().equalsIgnoreCase(name)) return index;
        return findStudentRecursive(name, index - 1);
    }

    // =========================================================================
    // EVENT HANDLERS - Course section  (untouched)
    // =========================================================================

    public void addCourse(ActionEvent e)
    {
        String code       = courseCodeField.getText().trim();
        String name       = courseNameField.getText().trim();
        String instructor = courseInstructorField.getText().trim();
        String creditsStr = courseCreditsField.getText().trim();
        String semester   = courseSemesterField.getText().trim();
        String prereqRaw  = coursePrereqField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || instructor.isEmpty()
                || creditsStr.isEmpty() || semester.isEmpty())
        {
            showToast("error", "Missing fields", "Code, Name, Instructor, Credits, and Semester are required. Prerequisites are optional.");
            return;
        }

        if (findCourseByCode(code) != null)
        {
            showToast("error", "Duplicate course code", "A course with code \"" + code + "\" already exists.");
            return;
        }

        int credits;
        try
        {
            credits = Integer.parseInt(creditsStr);
            if (credits <= 0) throw new NumberFormatException("must be positive");
        }
        catch (NumberFormatException ex)
        {
            showToast("error", "Invalid credit hours", "Credits must be a positive whole number. You entered: \"" + creditsStr + "\"");
            return;
        }

        if (!semester.equalsIgnoreCase("Fall") &&
            !semester.equalsIgnoreCase("Winter") &&
            !semester.equalsIgnoreCase("Both"))
        {
            showToast("error", "Invalid semester", "Semester must be \"Fall\", \"Winter\", or \"Both\". You entered: \"" + semester + "\"");
            return;
        }

        Course newCourse = new Course(code, name, instructor, credits, semester);

        if (!prereqRaw.isEmpty())
        {
            for (String part : prereqRaw.split(","))
            {
                String prereqCode = part.trim();
                if (!prereqCode.isEmpty()) newCourse.addPrerequisite(prereqCode);
            }
        }

        catalog.add(newCourse);
        admin.addCourse(newCourse);

        String message = "=== COURSE ADDED TO CATALOG ===\n\n" +
                         newCourse.displayInfo()                   +
                         "\n\nAdded by: " + admin.getName()       +
                         "\nCatalog now contains: " + catalog.size() + " course(s)";

        showToast("success", "Course added to catalog", message);
        writeToFile("NEW COURSE\n" + message);
        activityLog.push("Added course: " + code + " — " + name);

        courseCodeField.clear(); courseNameField.clear();
        courseInstructorField.clear(); courseCreditsField.clear();
        courseSemesterField.clear(); coursePrereqField.clear();
    }

    public void viewCatalog(ActionEvent e)
    {
        if (catalog.isEmpty())
        { showToast("info", "Catalog is empty", "Add some courses using the Course Catalog section first."); return; }

        int open = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("=== COURSE CATALOG ===\n\n");

        for (int i = 0; i < catalog.size(); i++)
        {
            Course c = catalog.get(i);
            if (c.hasSeatsAvailable()) open++;
            sb.append(i + 1).append(". ").append(c.displayInfo()).append("\n\n");
        }

        sb.append("─".repeat(46)).append("\n");
        sb.append("Total: ").append(catalog.size()).append(" courses  |  ");
        sb.append("Open: ").append(open).append("  |  ");
        sb.append("Full: ").append(catalog.size() - open);

        showToast("info", "Course catalog", sb.toString());
        writeToFile("=== VIEW CATALOG ===\n" + sb.toString());
        activityLog.push("Viewed catalog (" + catalog.size() + " courses)");
    }

    // =========================================================================
    // EVENT HANDLERS - Enroll / Drop  (untouched)
    // =========================================================================

    public void enrollStudentInCourse(ActionEvent e)
    {
        String studentIdStr = enrollStudentIdField.getText().trim();
        String courseCode   = enrollCourseCodeField.getText().trim();

        if (studentIdStr.isEmpty() || courseCode.isEmpty())
        { showToast("error", "Missing fields", "Please enter both a Student ID and a Course Code."); return; }

        int studentId;
        try { studentId = Integer.parseInt(studentIdStr); }
        catch (NumberFormatException ex)
        { showToast("error", "Invalid student ID", "ID must be a number. You entered: \"" + studentIdStr + "\""); return; }

        Student student = findStudentById(studentId);
        if (student == null)
        { showToast("error", "Student not found", "No student with ID " + studentId + ". Check the ID or register first."); return; }

        Course course = findCourseByCode(courseCode);
        if (course == null)
        { showToast("error", "Course not found", "No course with code \"" + courseCode + "\". Check the code or add it to the catalog."); return; }

        String missingPrereq = student.findMissingPrerequisite(course);
        boolean success = admin.processEnrollment(student, course);

        String result;
        if (success)
        {
            result = "=== ENROLLMENT SUCCESSFUL ===\n\n"                         +
                     "Course:  " + course.shortDisplay()                          + "\n" +
                     "Student: " + student.getName()                              +
                                 " (ID: " + student.getId() + ")\n\n"            +
                     "Student is now enrolled in " + student.getEnrolledCount()   +
                     " course(s) this term.\n\n"                                  +
                     student.displayInfo();
        }
        else
        {
            StringBuilder reason = new StringBuilder();
            reason.append("ENROLLMENT FAILED for \"").append(course.getName()).append("\"\n\n");
            reason.append("Reason may be one of:\n");

            if (!student.getIsEnrolled())
                reason.append("  - Student account is suspended\n");
            else if (student.getEnrolledCount() >= 6)
                reason.append("  - Student has reached the 6-course enrollment limit\n");
            else if (missingPrereq != null)
                reason.append("  - Missing prerequisite: ").append(missingPrereq)
                      .append(" (use Admin Panel to override if approved)\n");
            else if (!course.hasSeatsAvailable())
                reason.append("  - Course is full\n");
            else
                reason.append("  - Already enrolled in this course\n");

            reason.append("\nStudent status:\n").append(student.displayInfo());
            result = reason.toString();
        }

        showToast(success ? "success" : "error", success ? "Enrollment successful" : "Enrollment failed", result);
        writeToFile("ENROLLMENT\n" + result);
        activityLog.push("Enroll: \"" + course.getCode() + "\" → " + student.getName()
                         + (success ? " (success)" : " (failed)"));

        enrollStudentIdField.clear();
        enrollCourseCodeField.clear();
    }

    public void dropStudentFromCourse(ActionEvent e)
    {
        String studentIdStr = dropStudentIdField.getText().trim();
        String courseCode   = dropCourseCodeField.getText().trim();

        if (studentIdStr.isEmpty() || courseCode.isEmpty())
        { showToast("error", "Missing fields", "Please enter both a Student ID and a Course Code."); return; }

        int studentId;
        try { studentId = Integer.parseInt(studentIdStr); }
        catch (NumberFormatException ex)
        { showToast("error", "Invalid student ID", "ID must be a number. You entered: \"" + studentIdStr + "\""); return; }

        Student student = findStudentById(studentId);
        if (student == null)
        { showToast("error", "Student not found", "No student with ID " + studentId + "."); return; }

        Course course = findCourseByCode(courseCode);
        if (course == null)
        { showToast("error", "Course not found", "No course with code \"" + courseCode + "\"."); return; }

        boolean success = admin.processDrop(student, course);

        String result;
        if (success)
            result = "=== COURSE DROPPED ===\n\n"                           +
                     "Course:  " + course.shortDisplay()                    + "\n" +
                     "Student: " + student.getName()                        +
                                 " (ID: " + student.getId() + ")\n\n"      +
                     "Seat is now open for other students.\n\n"             +
                     student.displayInfo();
        else
            result = "DROP FAILED\n\n" +
                     student.getName() + " is not enrolled in \"" +
                     course.getName() + "\".\n\n" +
                     student.displayInfo();

        showToast(success ? "success" : "error", success ? "Course dropped" : "Drop failed", result);
        writeToFile("DROP\n" + result);
        activityLog.push("Drop: \"" + course.getCode() + "\" ← " + student.getName()
                         + (success ? " (success)" : " (failed)"));

        dropStudentIdField.clear();
        dropCourseCodeField.clear();
    }

    // =========================================================================
    // EVENT HANDLERS - Admin  (untouched)
    // =========================================================================

    public void generateReport(ActionEvent e)
    {
        String reportType = reportTypeBox.getValue();
        String report = admin.generateReport(reportType, students, catalog);
        showToast("info", "Report generated", report);
        writeToFile("REPORT GENERATED\n" + report);
        activityLog.push("Admin generated " + reportType + " report");
    }

    public void approvePrereqOverride(ActionEvent e)
    {
        String studentIdStr = overrideStudentIdField.getText().trim();
        String courseCode   = overrideCourseCodeField.getText().trim();

        if (studentIdStr.isEmpty() || courseCode.isEmpty())
        { showToast("error", "Missing fields", "Please enter both a Student ID and a Course Code."); return; }

        int studentId;
        try { studentId = Integer.parseInt(studentIdStr); }
        catch (NumberFormatException ex)
        { showToast("error", "Invalid student ID", "ID must be a number."); return; }

        Student student = findStudentById(studentId);
        if (student == null)
        { showToast("error", "Student not found", "No student with ID " + studentId + "."); return; }

        Course course = findCourseByCode(courseCode);
        if (course == null)
        { showToast("error", "Course not found", "No course with code \"" + courseCode + "\"."); return; }

        String result = "=== PREREQUISITE OVERRIDE ===\n\n" +
                        admin.approvePrereqOverride(student, course);

        showToast("success", "Prerequisite override", result);
        writeToFile("PREREQ OVERRIDE\n" + result);
        activityLog.push("Prereq override: " + courseCode + " for student " + studentId);

        overrideStudentIdField.clear();
        overrideCourseCodeField.clear();
    }

    public void showAdminInfo(ActionEvent e)
    {
        Person p = admin;
        showToast("info", "Admin info — polymorphism demo",
            "=== ADMIN INFO (POLYMORPHISM) ===\n\n"                                              +
            p.displayInfo()                                                                       +
            "\n\nRole: " + p.getRole()                                                           +
            "\n\n[Person reference 'p' holds a HeadAdvisor object at runtime]"                   +
            "\n[displayInfo() called on Person type → HeadAdvisor version runs]");
    }

    // =========================================================================
    // EVENT HANDLERS - Utility  (untouched)
    // =========================================================================

    public void showSampleStudent(ActionEvent e)
    {
        Person p = new Student("Alex Chen", 7001, "alex@unb.ca",
                               5551234, "Bachelor of Computer Science");
        showToast("info", "Polymorphism — Person → Student",
            "=== POLYMORPHISM: Person → Student ===\n\n"             +
            p.displayInfo()                                           +
            "\n\nRole: " + p.getRole()                               +
            "\n\n[Variable 'p' is declared as Person]"               +
            "\n[But it holds a Student object at runtime]"           +
            "\n[So p.displayInfo() calls Student's version of displayInfo()]");

        activityLog.push("Demonstrated polymorphism: Student");
    }

    public void showSampleAdvisor(ActionEvent e)
    {
        Person p = new RegularAdvisor("Sarah Johnson", 8001,
                                      "sarah@unb.ca", 5555678,
                                      "ADV-10", "Morning");
        showToast("info", "Polymorphism — Person → RegularAdvisor",
            "=== POLYMORPHISM: Person → RegularAdvisor ===\n\n"              +
            p.displayInfo()                                                    +
            "\n\nRole: " + p.getRole()                                        +
            "\n\n[Variable 'p' is declared as Person]"                        +
            "\n[But it holds a RegularAdvisor object at runtime]"             +
            "\n[So p.displayInfo() calls RegularAdvisor's version]");

        activityLog.push("Demonstrated polymorphism: RegularAdvisor");
    }

    public void showRecentActivity(ActionEvent e)
    {
        if (activityLog.empty())
        {
            showToast("info", "No activity yet", "Try registering a student or searching first.");
            return;
        }

        Stack<String> temp = new Stack<String>();
        StringBuilder sb = new StringBuilder();
        sb.append("=== RECENT ACTIVITY  (Stack — LIFO) ===\n");
        sb.append("Most recent action is shown FIRST\n\n");

        int count = 0;
        while (!activityLog.empty() && count < 3)
        {
            String action = activityLog.pop();
            sb.append(count + 1).append(". ").append(action).append("\n");
            temp.push(action);
            count++;
        }

        while (!temp.empty())
            activityLog.push(temp.pop());

        sb.append("\nTotal actions in log: ").append(activityLog.size());
        showToast("info", "Recent activity (Stack — LIFO)", sb.toString());
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args)
    {
        launch(args);
    }
}
