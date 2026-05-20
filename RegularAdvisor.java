/**
 * RegularAdvisor class represents standard academic advisors.
 * Extends the abstract Advisor class and implements all abstract methods.
 *
 * Regular advisors have standard permissions:
 *   - Can add courses to the catalog         (inherited from Advisor)
 *   - Can process enrollment transactions    (inherited from Advisor)
 *   - Can process drop transactions          (inherited from Advisor)
 *   - Can view transaction history           (inherited from Advisor)
 *   - CANNOT remove courses from catalog     (admin privilege only - HeadAdvisor)
 *
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version April 2026
 */
public class RegularAdvisor extends Advisor
{
    /**
     * Constructor for objects of class RegularAdvisor.
     * Calls parent Advisor constructor which calls Person constructor,
     * demonstrating the full three-level constructor chain:
     *   RegularAdvisor -> Advisor -> Person
     *
     * @param name        The advisor's full name
     * @param id          Unique person ID
     * @param email       Advisor's email address
     * @param phoneNumber Advisor's phone number
     * @param employeeId  Employee identification number
     * @param shift       Work shift assignment
     */
    public RegularAdvisor(String name, int id, String email, int phoneNumber,
                          String employeeId, String shift)
    {
        // Calls Advisor(name, id, email, phone, employeeId, shift)
        // which in turn calls Person(name, id, email, phone)
        super(name, id, email, phoneNumber, employeeId, shift);
    }

    // =========================================================================
    // ABSTRACT METHOD IMPLEMENTATIONS
    // =========================================================================

    /**
     * POLYMORPHISM - Implementation of abstract method from Advisor.
     * Regular advisors do NOT have permission to remove courses.
     * The HeadAdvisor override of this method returns true.
     *
     * Mirrors RegularLibrarian.canRemoveItems().
     *
     * @return false - regular advisors cannot remove items from the catalog
     */
    public boolean canRemoveCourses()
    {
        return false;
    }

    /**
     * POLYMORPHISM - Implementation of abstract method from Person.
     * Displays regular advisor details with standard permission level.
     *
     * Mirrors RegularLibrarian.displayInfo().
     *
     * @return Formatted string with advisor details
     */
    public String displayInfo()
    {
        return "Advisor:       " + getName()       +
               "\n\tEmployee ID:   " + getEmployeeId() +
               "\n\tEmail:         " + getEmail()       +
               "\n\tShift:         " + getShift()       +
               "\n\tPermissions:   Standard (cannot remove courses from catalog)";
    }

    /**
     * POLYMORPHISM - Implementation of abstract method from Person.
     * @return The role string for a regular advisor
     */
    public String getRole()
    {
        return "Academic Advisor";
    }
}
