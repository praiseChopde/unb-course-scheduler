import java.util.ArrayList;

/**
 * Course class represents a university course in the academic catalog.
 * Each course has a code, name, credit hours, semester offered, and
 * an optional list of prerequisite course codes.
 *
 * Courses can be enrolled in by students and dropped from their degree plan.
 * The Head Advisor can add or remove courses from the catalog.
 *
 * This class is the direct replacement for Book.java.
 * Mapping:
 *   Book.title      -> Course.name
 *   Book.author     -> Course.instructor
 *   Book.isbn       -> Course.code  (unique identifier, e.g. "CS2333")
 *   Book.isAvailable -> Course.hasSeatsAvailable
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version April 2026
 */
public class Course
{
    private String        code;           // unique course code, e.g. "CS2333"
    private String        name;           // full course name
    private String        instructor;     // professor's name
    private int           creditHours;    // credit hours this course is worth
    private String        semesterOffered;// "Fall", "Winter", or "Both"
    private boolean       hasSeatsAvailable; // true if students can still enroll
    private ArrayList<String> prerequisites;  // list of course codes required first

    /**
     * Constructor for objects of class Course.
     * All newly created courses start with seats available.
     *
     * @param code            The unique course code (e.g. "CS1083")
     * @param name            The full course name (e.g. "Data Structures")
     * @param instructor      The instructor's name
     * @param creditHours     Number of credit hours
     * @param semesterOffered When the course runs: "Fall", "Winter", or "Both"
     */
    public Course(String code, String name, String instructor,
                  int creditHours, String semesterOffered)
    {
        this.code             = code;
        this.name             = name;
        this.instructor       = instructor;
        this.creditHours      = creditHours;
        this.semesterOffered  = semesterOffered;
        this.hasSeatsAvailable = true;      // all new courses start open
        this.prerequisites    = new ArrayList<String>();
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    /**
     * Returns the unique code for this course.
     * @return The course code (e.g. "CS2333")
     */
    public String getCode()
    {
        return this.code;
    }

    /**
     * Returns the full name of this course.
     * @return The course name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the instructor teaching this course.
     * @return The instructor's name
     */
    public String getInstructor()
    {
        return this.instructor;
    }

    /**
     * Returns how many credit hours this course is worth.
     * @return The credit hour value
     */
    public int getCreditHours()
    {
        return this.creditHours;
    }

    /**
     * Returns when this course is offered.
     * @return "Fall", "Winter", or "Both"
     */
    public String getSemesterOffered()
    {
        return this.semesterOffered;
    }

    /**
     * Returns whether this course currently has seats available.
     * @return true if open for enrollment, false if full
     */
    public boolean hasSeatsAvailable()
    {
        return this.hasSeatsAvailable;
    }

    /**
     * Returns the list of prerequisite course codes.
     * @return ArrayList of course code strings (may be empty)
     */
    public ArrayList<String> getPrerequisites()
    {
        return this.prerequisites;
    }

    // =========================================================================
    // PREREQUISITE MANAGEMENT
    // =========================================================================

    /**
     * Adds a prerequisite course code to this course's requirements.
     * For example, addPrerequisite("CS1073") means CS1073 must be
     * completed before a student can enroll in this course.
     *
     * @param courseCode The code of the required prerequisite course
     */
    public void addPrerequisite(String courseCode)
    {
        if (!this.prerequisites.contains(courseCode))
        {
            this.prerequisites.add(courseCode);
        }
    }

    /**
     * Returns a readable string of all prerequisites, comma-separated.
     * Returns "None" if this course has no prerequisites.
     *
     * @return Formatted prerequisite list
     */
    public String getPrerequisitesDisplay()
    {
        if (this.prerequisites.isEmpty())
        {
            return "None";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.prerequisites.size(); i++)
        {
            sb.append(this.prerequisites.get(i));
            if (i < this.prerequisites.size() - 1)
            {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    // =========================================================================
    // ENROLLMENT ACTIONS
    // =========================================================================

    /**
     * Marks this course as full (no longer accepting enrollments).
     * Called when the last available seat is taken.
     *
     * Mirrors Book.checkOut() — same concept, enrollment instead of borrowing.
     *
     * @return true if enrollment succeeded, false if course was already full
     */
    public boolean enrollStudent()
    {
        if (this.hasSeatsAvailable)
        {
            this.hasSeatsAvailable = false;
            return true;
        }
        return false;   // course is already full
    }

    /**
     * Marks this course as having seats again (re-opens for enrollment).
     * Called when a student drops this course.
     *
     * Mirrors Book.returnBook() — same concept, dropping instead of returning.
     *
     * @return true if drop succeeded, false if course was already open
     */
    public boolean dropStudent()
    {
        if (!this.hasSeatsAvailable)
        {
            this.hasSeatsAvailable = true;
            return true;
        }
        return false;   // was already open
    }

    // =========================================================================
    // DISPLAY
    // =========================================================================

    /**
     * Returns a formatted multi-line display string for this course.
     * Used when showing catalog listings in the GUI.
     *
     * @return Multi-line string with all course details and availability
     */
    public String displayInfo()
    {
        return "Code:         " + this.code                    +
               "\n\tName:         " + this.name                +
               "\n\tInstructor:   " + this.instructor           +
               "\n\tCredits:      " + this.creditHours + " cr" +
               "\n\tSemester:     " + this.semesterOffered      +
               "\n\tPrereqs:      " + getPrerequisitesDisplay() +
               "\n\tStatus:       " + (this.hasSeatsAvailable
                                       ? "Open for Enrollment"
                                       : "Full");
    }

    /**
     * Returns a compact single-line summary of this course.
     * Used in student enrollment lists where space is limited.
     *
     * Mirrors Book.shortDisplay().
     *
     * @return Compact string with code, name, and credit hours
     */
    public String shortDisplay()
    {
        return this.code + " — " + this.name +
               " [" + this.creditHours + " cr]";
    }
}
