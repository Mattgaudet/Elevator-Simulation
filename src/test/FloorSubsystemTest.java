package test;

import floor.ElevatorRequest;
import floor.Floor;
import floor.FloorSubsystem;
import static test.Misc.assertWait;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the FloorSubsystem class.
 */
public class FloorSubsystemTest {
    private static int counter;
    private static FloorSubsystem floorSubsystem;

    /**
     * Set up initial conditions before running the tests.
     */
    @BeforeAll
    public static void setUp() {
        floorSubsystem = null;
        counter = 0;
    }

    /**
     * Clean up after all tests have been executed.
     */
    @AfterAll
    public static void tearDown() {
        System.out.println("All tests are done");
    }

    /**
     * Display a summary message after each test.
     */
    @AfterEach
    public void summary() {
        counter++;
        System.out.println("Number of tests run: " + counter);
    }

    /**
     * Test the changeLampStatus method of the FloorSubsystem class.
     */
    @Test
    void changeLampStatus() {
        floorSubsystem = new FloorSubsystem();
        floorSubsystem.changeLampStatus(ElevatorRequest.ButtonDirection.UP);

        for (Floor floor : floorSubsystem.getFloors()) {
            assertTrue(floor.getUpLampOnStatus());
            assertFalse(floor.getDownLampOnStatus());
        }
    }

    @Test
    void testWaitForRequestTriggered() {
        floorSubsystem = new FloorSubsystem("res/test_input1.csv", LocalTime.of(14, 0, 0));

        // NOTE: Lots of leniency here because CPU execution time adds to the baseline
        // time meaning that waiting durations get smaller.

        assertWait(() -> {
            ElevatorRequest er = floorSubsystem.waitForRequestTriggered();
            assertEquals(2, er.getFloorNumber());
        }, 0, 2);

        assertWait(() -> {
            ElevatorRequest er = floorSubsystem.waitForRequestTriggered();
            assertEquals(4, er.getFloorNumber());
        }, 1, 3);

        assertWait(() -> {
            ElevatorRequest er = floorSubsystem.waitForRequestTriggered();
            assertEquals(1, er.getFloorNumber());
        }, 3, 5);

        assertWait(() -> {
            ElevatorRequest er = floorSubsystem.waitForRequestTriggered();
            assertEquals(er, null);
        }, 0, 1);
    }
}
