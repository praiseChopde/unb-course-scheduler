/**
 * Abstract Advisor class - base for all academic advisor staff.
 * Extends Person and adds employee-specific attributes and shared operations.
 *
 * This class is abstract because there are two kinds of advisors with
 * different permission levels (RegularAdvisor and HeadAdvisor), and
 * neither should be created as a plain "Advisor".
 *
 * Direct mapping from Librarian.java:
 *   addItem(Book)              -> addCourse(Course)
 *   processBorrow(Member,Book) -> processEnrollment(Student,Course)
 *   processReturn(Member,Book) -> processDrop(Student,Course)
 *   viewTransactions()         -> viewTransactions()  (same name, new context)
 *   canRemoveItems()           -> canRemoveCourses()  (abstract - permission check)
 *
 * Shared advisor operations defined here (concrete, available to all staff):
 *   - addCourse()         : add a course to the catalog
 *   - processEnrollment() : enroll a student in a specific course
 *   - processDrop()       : drop a course for a student
 *   - viewTransactions()  : confirm transaction viewing
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version April 2026
 */
public abstract class Advisor extends Person
{
    private String employeeId;
    private String shift;

    /**
     * Constructor for objects of class Advisor.
     * Calls parent Person constructor and initializes employee-specific fields.
     *
     * Mirrors Librarian constructor exactly.
     *
     * @param name        The advisor's full name
     * @param id          Unique person ID
     * @param email       Advisor's email address
     * @param phoneNumber Advisor's phone number
     * @param employeeId  Employee identification number
     * @param shift       Work shift (e.g. "Morning", "Evening", "Full Day")
     */
    public Advisor(String name, int id, String email, int phoneNumber,
                   String employeeId, String shift)
    {
        // Call the parent Person constructor
        super(name, id, email, phoneNumber);

        this.employeeId = employeeId;
        this.shift      = shift;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    /**
     * Returns this advisor's employee ID.
     * @return The employee identification number
     */
    public String getEmployeeId()
    {
        return this.employeeId;
    }

    /**
     * Returns this advisor's work shift.
     * @return The shift string (e.g. "Morning")
     */
    public String getShift()
    {
        return this.shift;
    }

    // =========================================================================
    // SHARED CONCRETE OPERATIONS (available to all advisor types)
    // =========================================================================

    /**
     * Adds a new course to the academic catalog.
     * All advisor types can perform this action.
     *
     * Mirrors Librarian.addItem(Book).
     *
     * @param course The Course object to add to the catalog
     */
    public void addCourse(Course course)
    {
        System.out.println(getName() + " added course: " + course.getCode()
                           + " — " + course.getName());
    }

    /**
     * Processes an enrollment transaction: enrolls a student in a course.
     * Delegates the eligibility check to Student.enrollInCourse(course).
     *
     * Mirrors Librarian.processBorrow(Member, Book).
     *
     * @param student The student who wants to enroll
     * @param course  The specific course being enrolled in
     * @return true if enrollment succeeded, false otherwise
     */
    public boolean processEnrollment(Student student, Course course)
    {
        if (student.enrollInCourse(course))
        {
            System.out.println(getName() + " enrolled " + student.getName()
                               + " in " + course.getCode());
            return true;
        }
        else
        {
            System.out.println("Cannot enroll " + student.getName()
                               + " in " + course.getCode()
                               + " — check standing, limit, or prerequisites.");
            return false;
        }
    }

    /**
     * Processes a drop transaction: removes a course from a student's plan.
     *
     * Mirrors Librarian.processReturn(Member, Book).
     *
     * @param student The student dropping the course
     * @param course  The specific course being dropped
     * @return true if the drop succeeded, false if student wasn't enrolled
     */
    public boolean processDrop(Student student, Course course)
    {
        if (student.dropCourse(course))
        {
            System.out.println(getName() + " processed drop of "
                               + course.getCode() + " for " + student.getName());
            return true;
        }
        else
        {
            System.out.println("Drop failed: " + student.getName()
                               + " is not enrolled in " + course.getCode());
            return false;
        }
    }

    /**
     * Confirms that an advisor is viewing transaction history.
     * Placeholder for future database integration.
     *
     * Mirrors Librarian.viewTransactions().
     */
    public void viewTransactions()
    {
        System.out.println(getName() + " is viewing enrollment transaction history...");
    }

    // =========================================================================
    // ABSTRACT METHODS
    // =========================================================================

    /**
     * ABSTRACT - different advisor types have different course removal permissions.
     * RegularAdvisor returns false; HeadAdvisor returns true.
     *
     * Mirrors Librarian.canRemoveItems().
     *
     * @return true if this advisor type can remove courses from the catalog
     */
    public abstract boolean canRemoveCourses();

    /**
     * ABSTRACT - inherited from Person, must be implemented by each subclass.
     * @return Formatted display string for this advisor type
     */
    public abstract String displayInfo();

    /**
     * ABSTRACT - inherited from Person, must be implemented by each subclass.
     * @return The role string for this advisor type
     */
    public abstract String getRole();
}
