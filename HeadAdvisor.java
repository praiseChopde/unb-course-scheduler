import java.util.ArrayList;

/**
 * HeadAdvisor class represents the academic department administrator.
 * Extends the abstract Advisor class and implements all abstract methods.
 *
 * Head advisors have ALL standard permissions PLUS exclusive admin privileges:
 *   Standard (inherited from Advisor):
 *     - Add courses to catalog
 *     - Process enrollment / drop transactions
 *     - View transaction history
 *   Admin only:
 *     - Remove courses from catalog
 *     - Generate reports (student list, catalog, full system)
 *     - Approve prerequisite overrides for students
 *     - Manage staff
 *
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version April 2026
 */
public class HeadAdvisor extends Advisor
{
    /**
     * Constructor for objects of class HeadAdvisor.
     * Three-level constructor chain: HeadAdvisor -> Advisor -> Person.
     *
     *
     * @param name        The head advisor's full name
     * @param id          Unique person ID
     * @param email       Head advisor's email address
     * @param phoneNumber Head advisor's phone number
     * @param employeeId  Employee identification number
     * @param shift       Work shift assignment
     */
    public HeadAdvisor(String name, int id, String email, int phoneNumber,
                       String employeeId, String shift)
    {
        super(name, id, email, phoneNumber, employeeId, shift);
    }

    // =========================================================================
    // ABSTRACT METHOD IMPLEMENTATIONS
    // =========================================================================

    /**
     * POLYMORPHISM - Implementation of abstract method from Advisor.
     * Head advisors DO have permission to remove courses from the catalog.
     *
     * Mirrors HeadLibrarian.canRemoveItems().
     *
     * @return true - head advisors can remove catalog items
     */
    public boolean canRemoveCourses()
    {
        return true;
    }

    /**
     * POLYMORPHISM - Implementation of abstract method from Person.
     * Displays head advisor details with admin permission level.
     *
     * Mirrors HeadLibrarian.displayInfo().
     *
     * @return Formatted string with head advisor details
     */
    public String displayInfo()
    {
        return "Head Advisor:  " + getName()       +
               "\n\tEmployee ID:   " + getEmployeeId() +
               "\n\tEmail:         " + getEmail()       +
               "\n\tShift:         " + getShift()       +
               "\n\tPermissions:   ADMIN ACCESS (full catalog control + overrides)";
    }

    /**
     * POLYMORPHISM - Implementation of abstract method from Person.
     * @return The role string for the head advisor
     */
    public String getRole()
    {
        return "Head Advisor (Administrator)";
    }

    // =========================================================================
    // ADMIN-ONLY OPERATIONS
    // =========================================================================

    /**
     * ADMIN PRIVILEGE - Removes a specific course from the academic catalog.
     * Only head advisors may perform this action (canRemoveCourses() == true).
     *
     * Mirrors HeadLibrarian.removeBook(ArrayList<Book>, Book).
     *
     * @param catalog  The department's master course ArrayList
     * @param course   The Course to remove
     * @return true if the course was found and removed, false otherwise
     */
    public boolean removeCourse(ArrayList<Course> catalog, Course course)
    {
        if (canRemoveCourses())
        {
            if (catalog.remove(course))
            {
                System.out.println(getName() + " removed \"" + course.getCode()
                                   + "\" from catalog.");
                return true;
            }
            System.out.println("Course not found in catalog.");
            return false;
        }
        System.out.println("Permission denied: cannot remove courses.");
        return false;
    }

    /**
     * ADMIN PRIVILEGE - Generates a formatted report string.
     * Accepts the live student and course lists so the report contains real data.
     *
     * Three report types supported:
     *   "Students" - all registered students and their enrollment status
     *   "Catalog"  - all courses and their availability
     *   "Full"     - both of the above combined
     *
     * Mirrors HeadLibrarian.generateReport(String, ArrayList<Member>, ArrayList<Book>).
     *
     * @param reportType  "Students", "Catalog", or "Full"
     * @param students    The live ArrayList of Student objects
     * @param catalog     The live ArrayList of Course objects
     * @return            The full formatted report as a String
     */
    public String generateReport(String reportType,
                                 ArrayList<Student> students,
                                 ArrayList<Course>  catalog)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("  ACADEMIC REPORT: ").append(reportType.toUpperCase()).append("\n");
        sb.append("  Generated by: ").append(getName())
          .append(" (").append(getEmployeeId()).append(")\n");
        sb.append("========================================\n\n");

        if (reportType.equalsIgnoreCase("Students") || reportType.equalsIgnoreCase("Full"))
        {
            sb.append("--- STUDENTS (").append(students.size()).append(" registered) ---\n\n");

            if (students.isEmpty())
            {
                sb.append("  No students registered.\n");
            }
            else
            {
                for (int i = 0; i < students.size(); i++)
                {
                    sb.append("  ").append(i + 1).append(". ");
                    sb.append(students.get(i).displayInfo());
                    sb.append("\n\n");
                }
            }
        }

        if (reportType.equalsIgnoreCase("Catalog") || reportType.equalsIgnoreCase("Full"))
        {
            sb.append("--- COURSE CATALOG (").append(catalog.size()).append(" courses) ---\n\n");

            if (catalog.isEmpty())
            {
                sb.append("  No courses in catalog.\n");
            }
            else
            {
                int open = 0;
                for (Course c : catalog)
                {
                    if (c.hasSeatsAvailable()) open++;
                }

                sb.append("  Open: ").append(open)
                  .append(" / ").append(catalog.size()).append("\n\n");

                for (int i = 0; i < catalog.size(); i++)
                {
                    sb.append("  ").append(i + 1).append(". ");
                    sb.append(catalog.get(i).displayInfo());
                    sb.append("\n\n");
                }
            }
        }

        sb.append("========================================\n");
        sb.append("  END OF REPORT\n");
        sb.append("========================================");

        return sb.toString();
    }

    /**
     * ADMIN PRIVILEGE - Approves a prerequisite override for a student.
     * Allows a student to enroll in a course even if they are missing a
     * prerequisite, as a special exception granted by the head advisor.
     *
     * This is the conceptual replacement for HeadLibrarian.approveFineWaiver():
     *   Fine waiver   = removing a financial block
     *   Prereq override = removing an academic block
     * Same pattern, different domain.
     *
     * @param student     The student requesting the override
     * @param course      The course the student wants to enroll in despite missing prereqs
     * @return A status message describing the outcome
     */
    public String approvePrereqOverride(Student student, Course course)
    {
        // Check if override is actually needed
        String missingPrereq = student.findMissingPrerequisite(course);

        if (missingPrereq == null)
        {
            return "No override needed — " + student.getName() +
                   " already meets all prerequisites for " + course.getCode() + ".";
        }

        // Perform a direct enrollment bypass: add the course without prereq check
        // This is done by temporarily adding the missing prereq as completed
        // We use a workaround: directly add to enrolled after marking an override
        boolean success = course.enrollStudent();

        if (!success)
        {
            return "Override denied: " + course.getCode() + " is full.";
        }

        student.getEnrolledCourses().add(course);

        return "Prerequisite override APPROVED for " + student.getName()        +
               "\nCourse:          " + course.shortDisplay()                    +
               "\nMissing prereq:  " + missingPrereq + " (waived)"             +
               "\nApproved by:     " + getName()                                +
               "\nStudent is now enrolled in " + course.getCode() + ".";
    }

    /**
     * ADMIN PRIVILEGE - Logs a staff management action.
     *
     * Mirrors HeadLibrarian.manageStaff(String).
     *
     * @param action  Description of the management action performed
     */
    public void manageStaff(String action)
    {
        System.out.println(getName() + " performed staff action: " + action);
    }
}
