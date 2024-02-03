package test;

import floor.ElevatorRequest;
import floor.Floor;
import floor.FloorSubsystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scheduler.Scheduler;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Floor class.
 */
public class TestFloor {

    private static int counter;
    private static Floor floor;

    /**
     * Set up initial conditions before running the tests.
     */
    @BeforeAll
    public static void setUp() {
        floor = null;
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
    public void summery() {
        counter++;
        System.out.println("Number of tests run: " + counter);
    }

    /**
     * Test the getFloorNumber method of the Floor class.
     */
    @Test
    void getFloorNumber() {
        Scheduler scheduler = new Scheduler(new FloorSubsystem());
        floor = new Floor(1, scheduler);
        assertEquals(1, floor.getFloorNumber());
    }

    /**
     * Test the changeLampStatus method of the Floor class.
     */
    @Test
    void changeLampStatus() {
        Scheduler scheduler = new Scheduler(new FloorSubsystem());
        floor = new Floor(1, scheduler);

        floor.changeLampStatus(ElevatorRequest.ButtonDirection.UP);
        assertTrue(floor.isUpLampOn);
        assertFalse(floor.isDownLampOn);

        floor.changeLampStatus(ElevatorRequest.ButtonDirection.DOWN);
        assertFalse(floor.isUpLampOn);
        assertTrue(floor.isDownLampOn);

        floor.changeLampStatus(null);
        assertFalse(floor.isUpLampOn);
        assertFalse(floor.isDownLampOn);
    }
}

