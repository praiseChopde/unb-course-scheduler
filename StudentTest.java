import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Student.
 *
 * @author Praise Chopde
 * @studentID 3779552
 * @course CS1083
 * @version April 2026
 */
public class StudentTest
{
    private Student student1;
    private Student student2;
    private Course  cs1083;
    private Course  cs2333;

    public StudentTest()
    {
    }

    @BeforeEach
    public void setUp()
    {
        student1 = new Student("James Chopde", 3779552, "jchopde@unb.ca", 5061234, "Computer Science");
        student2 = new Student("Jane Doe",     1000001, "jdoe@unb.ca",    5065678, "Mathematics");

        cs1083 = new Course("CS1083", "Intro to Programming II", "Dr. Saha",     3, "Winter");
        cs2333 = new Course("CS2333", "Computability",           "Dr. Trenholm", 3, "Winter");
        cs2333.addPrerequisite("CS1083");
    }

    @AfterEach
    public void tearDown()
    {
        student1 = null;
        student2 = null;
        cs1083   = null;
        cs2333   = null;
    }

    @Test
    public void testGetName()
    {
        assertEquals("James Chopde", student1.getName());
    }

    @Test
    public void testGetId()
    {
        assertEquals(3779552, student1.getId());
    }

    @Test
    public void testGetProgram()
    {
        assertEquals("Computer Science", student1.getProgram());
    }

    @Test
    public void testGetRole()
    {
        assertEquals("Student — Computer Science", student1.getRole());
    }

    @Test
    public void testInitialEnrolledCountIsZero()
    {
        assertEquals(0, student1.getEnrolledCount());
    }

    @Test
    public void testEnrollInCourseSuccess()
    {
        boolean result = student1.enrollInCourse(cs1083);
        assertEquals(true, result);
        assertEquals(1, student1.getEnrolledCount());
    }

    @Test
    public void testEnrollInCourseIncreasesCount()
    {
        student1.enrollInCourse(cs1083);
        assertEquals(1, student1.getEnrolledCount());
    }

    @Test
    public void testEnrollDuplicateCourseBlocked()
    {
        student1.enrollInCourse(cs1083);
        boolean duplicate = student1.enrollInCourse(cs1083);
        assertEquals(false, duplicate);
        assertEquals(1, student1.getEnrolledCount());
    }

    @Test
    public void testEnrollBlockedByMissingPrerequisite()
    {
        // cs2333 requires CS1083 — student hasn't completed it
        boolean result = student1.enrollInCourse(cs2333);
        assertEquals(false, result);
        assertEquals(0, student1.getEnrolledCount());
    }

    @Test
    public void testEnrollAllowedAfterPrereqCompleted()
    {
        // complete CS1083 first, then enroll in CS2333
        student1.enrollInCourse(cs1083);
        student1.completeCourse(cs1083);
        boolean result = student1.enrollInCourse(cs2333);
        assertEquals(true, result);
    }

    @Test
    public void testDropCourseSuccess()
    {
        student1.enrollInCourse(cs1083);
        boolean dropped = student1.dropCourse(cs1083);
        assertEquals(true, dropped);
        assertEquals(0, student1.getEnrolledCount());
    }

    @Test
    public void testDropCourseNotEnrolled()
    {
        // student is not enrolled in cs1083 — drop should return false
        boolean dropped = student1.dropCourse(cs1083);
        assertEquals(false, dropped);
    }

    @Test
    public void testCompleteCourseMovesToCompleted()
    {
        student1.enrollInCourse(cs1083);
        student1.completeCourse(cs1083);
        assertEquals(0, student1.getEnrolledCount());
        assertEquals(1, student1.getCompletedCourses().size());
    }

    @Test
    public void testHasCompletedTrue()
    {
        student1.enrollInCourse(cs1083);
        student1.completeCourse(cs1083);
        assertEquals(true, student1.hasCompleted("CS1083"));
    }

    @Test
    public void testHasCompletedFalse()
    {
        assertEquals(false, student1.hasCompleted("CS1083"));
    }

    @Test
    public void testGetTotalCreditsCompleted()
    {
        student1.enrollInCourse(cs1083);
        student1.completeCourse(cs1083);
        assertEquals(3, student1.getTotalCreditsCompleted());
    }

    @Test
    public void testSetGpaValid()
    {
        student1.setGpa(3.8);
        assertEquals(3.8, student1.getGpa());
    }

    @Test
    public void testSetGpaInvalidIgnored()
    {
        student1.setGpa(3.5);
        student1.setGpa(5.0); // invalid — above 4.0
        assertEquals(3.5, student1.getGpa());
    }
}
