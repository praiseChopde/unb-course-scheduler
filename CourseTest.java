import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Course.
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version April 2026
 */
public class CourseTest
{
    private Course cs1083;
    private Course cs2333;

    public CourseTest()
    {
    }

    @BeforeEach
    public void setUp()
    {
        cs1083 = new Course("CS1083", "Intro to Programming II", "Dr. Saha", 3, "Winter");
        cs2333 = new Course("CS2333", "Computability", "Dr. Trenholm", 3, "Winter");
        cs2333.addPrerequisite("CS1083");
    }

    @AfterEach
    public void tearDown()
    {
        cs1083 = null;
        cs2333 = null;
    }

    @Test
    public void testGetCode()
    {
        assertEquals("CS1083", cs1083.getCode());
        assertEquals("CS2333", cs2333.getCode());
    }

    @Test
    public void testGetCreditHours()
    {
        assertEquals(3, cs1083.getCreditHours());
    }

    @Test
    public void testHasSeatsAvailableInitially()
    {
        // a new course should have seats available by default
        assertEquals(true, cs1083.hasSeatsAvailable());
    }

    @Test
    public void testEnrollStudentMakesCourseFulll()
    {
        cs1083.enrollStudent();
        assertEquals(false, cs1083.hasSeatsAvailable());
    }

    @Test
    public void testDropStudentReopensSeats()
    {
        cs1083.enrollStudent();
        cs1083.dropStudent();
        assertEquals(true, cs1083.hasSeatsAvailable());
    }

    @Test
    public void testAddPrerequisite()
    {
        // cs2333 already has CS1083 as prereq from setUp
        assertEquals("CS1083", cs2333.getPrerequisites().get(0));
    }

    @Test
    public void testAddDuplicatePrerequisiteIgnored()
    {
        cs2333.addPrerequisite("CS1083"); // duplicate — should not be added again
        assertEquals(1, cs2333.getPrerequisites().size());
    }

    @Test
    public void testGetPrerequisitesDisplayNone()
    {
        // cs1083 has no prerequisites
        assertEquals("None", cs1083.getPrerequisitesDisplay());
    }

    @Test
    public void testGetPrerequisitesDisplayWithValue()
    {
        assertEquals("CS1083", cs2333.getPrerequisitesDisplay());
    }
}
