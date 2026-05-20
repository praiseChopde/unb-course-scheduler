import java.util.ArrayList;

/**
 * Student class represents a university student who can enroll in courses
 * and build their degree plan.
 * Extends the abstract Person class and implements all abstract methods.
 *
 *
 * Key OOP concepts demonstrated:
 *   - INHERITANCE    : extends Person
 *   - POLYMORPHISM   : overrides displayInfo() and getRole()
 *   - ENCAPSULATION  : private fields with public getters/methods
 *   - COLLECTIONS    : ArrayList<Course> for enrolled and completed courses
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version April 2026
 */
public class Student extends Person
{
    // ArrayList<Course> tracks the courses this student is currently taking.
    // Using ArrayList instead of a fixed array means the list grows automatically
    // and we always know the exact count via enrolledCourses.size().
    private ArrayList<Course> enrolledCourses;

    // ArrayList<Course> tracks every course the student has already passed.
    // This is how prerequisite checking works: before enrolling in a course,
    // the system checks if its prerequisites appear in this list.
    private ArrayList<Course> completedCourses;

    // Academic standing: replaces double fines from Member.java
    private double gpa;

    // Whether this student's account is in good standing
    private boolean isEnrolled;

    // Program of study (e.g. "Bachelor of Computer Science")
    private String program;

    // Class constant - maximum number of courses per semester
    private static final int MAX_COURSES = 6;

    /**
     * Constructor for objects of class Student.
     * Calls the parent Person constructor and initializes student-specific fields.
     *
     * @param name        The student's full name
     * @param id          Unique student ID number
     * @param email       Student's email address
     * @param phoneNumber Student's phone number
     * @param program     The student's program (e.g. "Bachelor of Computer Science")
     */
    public Student(String name, int id, String email, int phoneNumber, String program)
    {
        // Call the parent (Person) constructor first
        super(name, id, email, phoneNumber);

        // Initialize student-specific fields
        this.enrolledCourses  = new ArrayList<Course>();
        this.completedCourses = new ArrayList<Course>();
        this.gpa              = 0.0;
        this.isEnrolled       = true;
        this.program          = program;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    /**
     * Returns how many courses this student is currently enrolled in.
     * Delegates to ArrayList.size() so it is always accurate.
     *
     * Mirrors Member.getItemsBorrowed().
     *
     * @return Number of courses currently enrolled in
     */
    public int getEnrolledCount()
    {
        return this.enrolledCourses.size();
    }

    /**
     * Returns the list of courses this student is currently enrolled in.
     * Used by the GUI to display specific course titles.
     *
     * Mirrors Member.getBorrowedBooks().
     *
     * @return ArrayList of Course objects currently enrolled in
     */
    public ArrayList<Course> getEnrolledCourses()
    {
        return this.enrolledCourses;
    }

    /**
     * Returns the list of courses this student has already completed.
     * Used for prerequisite checking.
     *
     * @return ArrayList of Course objects already completed
     */
    public ArrayList<Course> getCompletedCourses()
    {
        return this.completedCourses;
    }

    /**
     * Returns the student's current GPA.
     * Replaces Member.getFines().
     *
     * @return The student's GPA on a 4.0 scale
     */
    public double getGpa()
    {
        return this.gpa;
    }

    /**
     * Returns whether this student's account is in good standing.
     * Mirrors Member.getIsActive().
     *
     * @return true if enrolled, false if suspended
     */
    public boolean getIsEnrolled()
    {
        return this.isEnrolled;
    }

    /**
     * Returns the student's program of study.
     *
     * @return The program name string
     */
    public String getProgram()
    {
        return this.program;
    }

    /**
     * Returns the total number of credit hours the student has completed.
     * Calculated by summing creditHours across all completedCourses.
     *
     * @return Total completed credit hours
     */
    public int getTotalCreditsCompleted()
    {
        int total = 0;
        for (Course c : completedCourses)
        {
            total += c.getCreditHours();
        }
        return total;
    }

    /**
     * Returns the total credit hours the student is currently enrolled in.
     *
     * @return Total enrolled credit hours this term
     */
    public int getCurrentCreditLoad()
    {
        int total = 0;
        for (Course c : enrolledCourses)
        {
            total += c.getCreditHours();
        }
        return total;
    }

    // =========================================================================
    // PREREQUISITE CHECKING
    // =========================================================================

    /**
     * Checks whether this student has completed a specific course code.
     * Used during prerequisite validation before enrollment.
     *
     * @param courseCode The course code to look for in completedCourses
     * @return true if the student has completed that course, false otherwise
     */
    public boolean hasCompleted(String courseCode)
    {
        for (Course c : completedCourses)
        {
            if (c.getCode().equalsIgnoreCase(courseCode))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether this student meets ALL prerequisites for a given course.
     * Returns the name of the first missing prerequisite if one is found,
     * or null if all prerequisites are satisfied.
     *
     * @param course The course to check prerequisites for
     * @return null if eligible, or the missing prerequisite code as a String
     */
    public String findMissingPrerequisite(Course course)
    {
        for (String prereqCode : course.getPrerequisites())
        {
            if (!hasCompleted(prereqCode))
            {
                return prereqCode;   // found a missing prereq
            }
        }
        return null;   // all prerequisites satisfied
    }

    // =========================================================================
    // ENROLL AND DROP
    // =========================================================================

    /**
     * Attempts to enroll this student in a specific course.
     *
     * Checks four eligibility conditions before allowing enrollment:
     *   1. Student account must be in good standing (isEnrolled == true)
     *   2. Student must be under the MAX_COURSES limit
     *   3. Student must not already be enrolled in this exact course
     *   4. All prerequisites for this course must be satisfied
     *
     * If eligible, calls course.enrollStudent() to mark the course full,
     * then adds it to this student's enrolled list.
     *
     * Mirrors Member.borrowItem(Book).
     *
     * @param course The Course object the student wants to enroll in
     * @return true if enrollment succeeded, false if any condition was not met
     */
    public boolean enrollInCourse(Course course)
    {
        // Condition 1: account must be active
        if (!isEnrolled)
        {
            return false;   // account suspended
        }

        // Condition 2: must be under the course limit
        if (enrolledCourses.size() >= MAX_COURSES)
        {
            return false;   // at enrollment limit
        }

        // Condition 3: must not already be enrolled in this course
        for (Course c : enrolledCourses)
        {
            if (c.getCode().equalsIgnoreCase(course.getCode()))
            {
                return false;   // already enrolled
            }
        }

        // Condition 4: all prerequisites must be satisfied
        if (findMissingPrerequisite(course) != null)
        {
            return false;   // missing a prerequisite
        }

        // Attempt to enroll (course might already be full)
        if (!course.enrollStudent())
        {
            return false;   // course was already full
        }

        // All checks passed - add to this student's list
        enrolledCourses.add(course);
        return true;
    }

    /**
     * Drops a specific course from this student's enrolled list.
     * Removes the course from the enrolled list and calls
     * course.dropStudent() to mark the seat as available again.
     *
     * Mirrors Member.returnItem(Book).
     *
     * @param course The Course object to drop
     * @return true if the drop succeeded, false if this student wasn't enrolled
     */
    public boolean dropCourse(Course course)
    {
        // Check if this student is actually enrolled in this course
        if (enrolledCourses.remove(course))
        {
            course.dropStudent();   // mark the seat as open again
            return true;
        }
        return false;   // student wasn't enrolled in this course
    }

    /**
     * Marks a currently enrolled course as completed.
     * Moves the course from enrolledCourses to completedCourses so it
     * counts toward future prerequisite checks.
     *
     * @param course The Course the student has finished
     * @return true if the course was moved, false if student wasn't enrolled
     */
    public boolean completeCourse(Course course)
    {
        if (enrolledCourses.remove(course))
        {
            completedCourses.add(course);
            course.dropStudent();   // free the seat
            return true;
        }
        return false;
    }

    // =========================================================================
    // GPA
    // =========================================================================

    /**
     * Sets the student's GPA.
     * Replaces Member.addFine() / payFine() with academic standing updates.
     *
     * @param newGpa The new GPA value (must be between 0.0 and 4.0)
     */
    public void setGpa(double newGpa)
    {
        if (newGpa >= 0.0 && newGpa <= 4.0)
        {
            this.gpa = newGpa;
        }
    }

    // =========================================================================
    // SETTERS
    // =========================================================================

    /**
     * Sets the enrollment/suspension status of this student's account.
     * Mirrors Member.setIsActive(boolean).
     *
     * @param status true to activate, false to suspend
     */
    public void setIsEnrolled(boolean status)
    {
        this.isEnrolled = status;
    }

    /**
     * Sets the student's program of study.
     *
     * @param program The program name to set
     */
    public void setProgram(String program)
    {
        this.program = program;
    }

    // =========================================================================
    // ABSTRACT METHOD IMPLEMENTATIONS (required by Person)
    // =========================================================================

    /**
     * POLYMORPHISM - Implementation of abstract method from Person.
     * Provides student-specific display, including the list of enrolled courses
     * and completed courses.
     *
     * Mirrors Member.displayInfo() — same structure, new field names.
     *
     * @return Formatted string with all student details
     */
    public String displayInfo()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Student:  ").append(getName());
        sb.append("\n\tID:       ").append(getId());
        sb.append("\n\tProgram:  ").append(program);
        sb.append("\n\tEmail:    ").append(getEmail());
        sb.append("\n\tPhone:    ").append(getPhoneNumber());
        sb.append("\n\tGPA:      ").append(String.format("%.2f", gpa));
        sb.append("\n\tStatus:   ").append(isEnrolled ? "In Good Standing" : "Suspended");
        sb.append("\n\tCredits Completed: ").append(getTotalCreditsCompleted()).append(" cr");
        sb.append("\n\tCurrent Load:      ").append(getCurrentCreditLoad()).append(" cr");

        sb.append("\n\tEnrolled Courses (")
          .append(enrolledCourses.size()).append("/").append(MAX_COURSES).append("):");

        if (enrolledCourses.isEmpty())
        {
            sb.append("\n\t  (none)");
        }
        else
        {
            for (Course c : enrolledCourses)
            {
                sb.append("\n\t  - ").append(c.shortDisplay());
            }
        }

        sb.append("\n\tCompleted Courses (").append(completedCourses.size()).append("):");

        if (completedCourses.isEmpty())
        {
            sb.append("\n\t  (none)");
        }
        else
        {
            for (Course c : completedCourses)
            {
                sb.append("\n\t  - ").append(c.shortDisplay());
            }
        }

        return sb.toString();
    }

    /**
     * POLYMORPHISM - Implementation of abstract method from Person.
     * @return The role string for this person type
     */
    public String getRole()
    {
        return "Student — " + program;
    }
}
