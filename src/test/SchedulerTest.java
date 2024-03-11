package test;

import floor.ElevatorRequest;
import floor.FloorSubsystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import scheduler.ProcessingRequestState;
import scheduler.Scheduler;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the Scheduler class.
 */
public class SchedulerTest {
    private static int counter;
    private static Scheduler scheduler;

    /**
     * Set up initial conditions before running the tests.
     */
    @BeforeAll
    public static void setUp() {
        scheduler = null;
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
     * Test the addToRequestQueue method of the Scheduler class.
     */
    @Test
    void addToRequestQueue() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        scheduler = new Scheduler(floorSubsystem);
        ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 3, ElevatorRequest.ButtonDirection.UP,4);

        scheduler.addToRequestQueue(elevatorRequest);
        ArrayList<ElevatorRequest> requestQueue = scheduler.getRequestQueueFromScheduler();

        assertTrue(requestQueue.contains(elevatorRequest));
    }

    /**
     * Test the run method of the Scheduler class.
     */
    @Test
    void run() {
        FloorSubsystem floorSubsystem = new FloorSubsystem("res/test_input2.csv", LocalTime.of(14, 0));
        scheduler = new Scheduler(floorSubsystem);
        floorSubsystem.setScheduler(scheduler);
        Thread schedulerThread = new Thread(scheduler);  // this is just to start the scheduler in a separate thread
        Thread floorSubsystemThread = new Thread(floorSubsystem);
        schedulerThread.start();
        floorSubsystemThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<ElevatorRequest> requestQueue = scheduler.getRequestQueueFromScheduler();
        assertEquals(requestQueue.get(0).getFloorNumber(), 2);
    }

        @Test
    public void testSelectElevator() {
        // Mock elevator information
        String mockElevatorInfo = "1;IDLE;3;UP\n2;TRANSPORTING;5;DOWN\n3;IDLE;2;UP\n";

        // Create a ProcessingRequestState instance for testing
        ProcessingRequestState processingRequestState = new ProcessingRequestState();

        // Test case 1: Create a mock request going up from floor 3 to 10, so it should use elevator id number 1 since elevator 1 is in floor 3
        ElevatorRequest mockRequest1 = new ElevatorRequest(LocalTime.now(),3, ElevatorRequest.ButtonDirection.UP,10);
        int selectedElevatorId1 = processingRequestState.selectElevator(scheduler, mockRequest1, mockElevatorInfo);
        assertEquals(1, selectedElevatorId1); // Elevator 1 is closest and idle

        // Test case 2: Create a mock request going down from floor 6 to 2, so it should use elevator id number 2 since elevator 2 is in floor 5 and going down
        ElevatorRequest mockRequest2 = new ElevatorRequest(LocalTime.now(),6, ElevatorRequest.ButtonDirection.DOWN,2);
        int selectedElevatorId2 = processingRequestState.selectElevator(scheduler, mockRequest2, mockElevatorInfo);
        assertEquals(2, selectedElevatorId2); // Elevator 2 is closest and idle

    }
}

