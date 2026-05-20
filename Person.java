/**
 * This class cannot be instantiated directly. It forces all subclasses to
 * implement displayInfo() and getRole(), ensuring every person type can
 * describe itself - this is the foundation of POLYMORPHISM in this system.
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version March 2026
 */
public abstract class Person
{
    private String name;
    private int    id;
    private String email;
    private int    phoneNumber;

    /**
     * Constructor for objects of class Person.
     * Called by subclass constructors using super().
     *
     * @param name        The person's full name
     * @param id          Unique identifier for this person
     * @param email       Email address
     * @param phoneNumber Contact phone number
     */
    public Person(String name, int id, String email, int phoneNumber)
    {
        this.name        = name;
        this.id          = id;
        this.email       = email;
        this.phoneNumber = phoneNumber;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    /**
     * Returns the name of this person.
     * @return The person's full name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the ID of this person.
     * @return The person's unique ID
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Returns the email of this person.
     * @return The person's email address
     */
    public String getEmail()
    {
        return this.email;
    }

    /**
     * Returns the phone number of this person.
     * @return The person's phone number
     */
    public int getPhoneNumber()
    {
        return this.phoneNumber;
    }

    // =========================================================================
    // SETTERS
    // =========================================================================

    /**
     * Sets a new name for this person.
     * @param newName The new name to set
     */
    public void setName(String newName)
    {
        this.name = newName;
    }

    /**
     * Sets a new ID for this person.
     * @param newID The new ID to set
     */
    public void setId(int newID)
    {
        this.id = newID;
    }

    /**
     * Sets a new email for this person.
     * @param newEmail The new email to set
     */
    public void setEmail(String newEmail)
    {
        this.email = newEmail;
    }

    /**
     * Sets a new phone number for this person.
     * @param newPhoneNumber The new phone number to set
     */
    public void setPhoneNumber(int newPhoneNumber)
    {
        this.phoneNumber = newPhoneNumber;
    }

    // =========================================================================
    // CONCRETE METHOD
    // =========================================================================

    /**
     * Returns a basic formatted display of this person's common fields.
     * Available to all subclasses without requiring an override.
     *
     * @return Formatted string with name, id, email, and phone
     */
    public String getDisplay()
    {
        return "Name: "   + this.name        +
               "\n\tId: "    + this.id          +
               "\n\tEmail: " + this.email       +
               "\n\tPhone: " + this.phoneNumber;
    }

    // =========================================================================
    // ABSTRACT METHODS - must be implemented by every concrete subclass
    // =========================================================================

    /**
     * ABSTRACT - forces each subclass to provide its own display.
     * This is the key to POLYMORPHISM: same method name, different behaviour
     * depending on the actual runtime type of the object.
     *
     * @return Formatted display string specific to the person's role
     */
    public abstract String displayInfo();

    /**
     * ABSTRACT - forces each subclass to declare what role it plays.
     * @return The role/type of this person in the library system
     */
    public abstract String getRole();
}
