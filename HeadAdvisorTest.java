import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

/**
 * Test class for HeadAdvisor.
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version April 2026
 */
public class HeadAdvisorTest
{
    private HeadAdvisor    headAdvisor;
    private RegularAdvisor regularAdvisor;
    private Student        student1;
    private Course         cs1083;
    private Course         cs2333;
    private ArrayList<Course>   catalog;
    private ArrayList<Student>  students;

    public HeadAdvisorTest()
    {
    }

    @BeforeEach
    public void setUp()
    {
        headAdvisor    = new HeadAdvisor("Praise Chopde", 9001, "pchopde@unb.ca", 5069001, "ADV-01", "Full Day");
        regularAdvisor = new RegularAdvisor("Dr. Smith",  9002, "smith@unb.ca",   5069002, "ADV-02", "Morning");

        student1 = new Student("James Chopde", 3779552, "jchopde@unb.ca", 5061234, "Computer Science");

        cs1083 = new Course("CS1083", "Intro to Programming II", "Dr. Saha",     3, "Winter");
        cs2333 = new Course("CS2333", "Computability",           "Dr. Trenholm", 3, "Winter");
        cs2333.addPrerequisite("CS1083");

        catalog  = new ArrayList<Course>();
        students = new ArrayList<Student>();
        catalog.add(cs1083);
        catalog.add(cs2333);
        students.add(student1);
    }

    @AfterEach
    public void tearDown()
    {
        headAdvisor    = null;
        regularAdvisor = null;
        student1       = null;
        cs1083         = null;
        cs2333         = null;
        catalog        = null;
        students       = null;
    }

    @Test
    public void testGetName()
    {
        assertEquals("Praise Chopde", headAdvisor.getName());
    }

    @Test
    public void testGetRole()
    {
        assertEquals("Head Advisor (Administrator)", headAdvisor.getRole());
    }

    @Test
    public void testRegularAdvisorGetRole()
    {
        assertEquals("Academic Advisor", regularAdvisor.getRole());
    }

    @Test
    public void testHeadAdvisorCanRemoveCourses()
    {
        // HeadAdvisor has admin privilege — Chapter 10 polymorphism
        assertEquals(true, headAdvisor.canRemoveCourses());
    }

    @Test
    public void testRegularAdvisorCannotRemoveCourses()
    {
        // RegularAdvisor does not have this privilege — polymorphism
        assertEquals(false, regularAdvisor.canRemoveCourses());
    }

    @Test
    public void testRemoveCourseSuccess()
    {
        boolean removed = headAdvisor.removeCourse(catalog, cs1083);
        assertEquals(true, removed);
        assertEquals(1, catalog.size());
    }

    @Test
    public void testRemoveCourseReducesCatalogSize()
    {
        headAdvisor.removeCourse(catalog, cs1083);
        assertEquals(1, catalog.size());
    }

    @Test
    public void testProcessEnrollmentSuccess()
    {
        boolean enrolled = headAdvisor.processEnrollment(student1, cs1083);
        assertEquals(true, enrolled);
    }

    @Test
    public void testProcessEnrollmentBlockedByPrereq()
    {
        // cs2333 needs CS1083 — not completed yet
        boolean enrolled = headAdvisor.processEnrollment(student1, cs2333);
        assertEquals(false, enrolled);
    }

    @Test
    public void testProcessDropSuccess()
    {
        headAdvisor.processEnrollment(student1, cs1083);
        boolean dropped = headAdvisor.processDrop(student1, cs1083);
        assertEquals(true, dropped);
    }

    @Test
    public void testProcessDropNotEnrolled()
    {
        boolean dropped = headAdvisor.processDrop(student1, cs1083);
        assertEquals(false, dropped);
    }

    @Test
    public void testGenerateReportContainsStudentName()
    {
        String report = headAdvisor.generateReport("Students", students, catalog);
        assertEquals(true, report.contains("James Chopde"));
    }

    @Test
    public void testGenerateReportContainsCourseCode()
    {
        String report = headAdvisor.generateReport("Catalog", students, catalog);
        assertEquals(true, report.contains("CS1083"));
    }
}
