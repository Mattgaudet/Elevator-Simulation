package test;

import static org.junit.jupiter.api.Assertions.*;

import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scheduler.Scheduler;
import floor.ElevatorRequest;
import floor.ElevatorRequest.ButtonDirection;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * JUnit tests for the ElevatorSubsystem class.
 */
class TestElevatorSubsystem {
    private static int counter;
    private static ElevatorSubsystem elevatorSubsystem;

    /**
     * Set up initial conditions before running the tests.
     */
    @BeforeAll
    public static void setUp() {
        elevatorSubsystem = null;
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
     * Test the initialization of the ElevatorSubsystem class.
     */
    @Test
    void testElevatorSubsystemInitialization() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        Scheduler scheduler = new Scheduler(floorSubsystem);
        elevatorSubsystem = new ElevatorSubsystem(scheduler);

        assertNotNull(elevatorSubsystem);
        assertNotNull(elevatorSubsystem.getElevatorSubsystemRequestsQueue());
        assertEquals(0, elevatorSubsystem.getElevatorSubsystemRequestsQueue().size());
    }

    /**
     * Test the run method of the ElevatorSubsystem class.
     */
    @Test
    void testElevatorSubsystemRun() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        Scheduler scheduler = new Scheduler(floorSubsystem);
        elevatorSubsystem = new ElevatorSubsystem(scheduler);

        Thread elevatorSubsystemThread = new Thread(elevatorSubsystem);
        elevatorSubsystemThread.start();

        ElevatorRequest request = new ElevatorRequest(LocalTime.now(), 1, ButtonDirection.UP,2);
        scheduler.addToRequestQueue(request);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //assertEquals(1, elevatorSubsystem.getElevatorSubsystemRequestsQueue().size());
    }

    /**
     * Test the addResponseList method of the ElevatorSubsystem class.
     */
    @Test
    void testAddResponseList() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        Scheduler scheduler = new Scheduler(floorSubsystem);
        elevatorSubsystem = new ElevatorSubsystem(scheduler);

        ElevatorRequest response = new ElevatorRequest(LocalTime.now(), 1, ButtonDirection.UP, 2);
        elevatorSubsystem.addResponseList(response);

        assertEquals(1, elevatorSubsystem.getElevatorSubsystemResponseLog().size());
        assertEquals(response, elevatorSubsystem.getElevatorSubsystemResponseLog().get(0));
    }
}
