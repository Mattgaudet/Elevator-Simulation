package test;

import floor.ElevatorRequest;
import floor.Floor;
import floor.FloorSubsystem;
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
     * Test the addIn and removeOut methods of the FloorSubsystem class.
     */
    @Test
    void addInAndRemoveOut() {
        floorSubsystem = new FloorSubsystem();
        ElevatorRequest buttonPress = new ElevatorRequest(LocalTime.now(), 1, ElevatorRequest.ButtonDirection.UP,1);

        floorSubsystem.addIn(buttonPress);
        assertEquals(1, floorSubsystem.getAllElevatorRequestsFromFloorSubsystem().size());

        floorSubsystem.removeOut(0);
        assertTrue(floorSubsystem.getAllElevatorRequestsFromFloorSubsystem().isEmpty());
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
}
