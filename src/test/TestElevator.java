package test;

import static org.junit.jupiter.api.Assertions.*;

import elevator.Elevator;
import elevator.ElevatorSubsystem;
import elevator.ElevatorTransportingState;
import floor.CSVParser;
import floor.ElevatorRequest;
import floor.FloorSubsystem;
import scheduler.Scheduler;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * JUnit tests for the Elevator class.
 */
public class TestElevator {
    private static int counter;
    private static Elevator elevator;
    private static ElevatorSubsystem elevatorSubsystem;
    private static Scheduler scheduler;
    private static FloorSubsystem floorSubsystem;

    
    /**
     * Set up initial conditions before running the tests.
     */
    @BeforeAll
    public static void setUp() {
        elevator = null;
        counter = 0;
        floorSubsystem = new FloorSubsystem();
        scheduler = new Scheduler(floorSubsystem);
        elevatorSubsystem = new ElevatorSubsystem(scheduler, 1);
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
     * Test the simulateElevatorMovement method of the Elevator class.
     */
    @Test
    void testSimulateElevatorMovement() {
        elevator = new Elevator(1, elevatorSubsystem);
        elevator.start();
        ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 3, ElevatorRequest.ButtonDirection.UP, 4);
        elevator.addRequestToElevatorQueue(elevatorRequest);
        try {
            Thread.sleep(21000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(4, elevator.getCurrentFloor());
    }

    /**
     * Test the arrivedFloor method of the Elevator class.
     */
    @Test
    void testArrivedFloor() {
        elevator = new Elevator(1, elevatorSubsystem);
        assertEquals(-1, elevator.arrivedFloor(3));
        assertEquals(3, elevator.getCurrentFloor());
    }

    /**
     * Test the findTravelTime method of the Elevator class.
     */
    @Test
    void testFindTravelTime() {
        elevator = new Elevator(1, elevatorSubsystem);
        assertEquals(2000, elevator.findTravelTime(4));
    }

    /**
     * Test the timeToLoadPassengers method of the Elevator class.
     */
    @Test
    void testTimeToLoadPassengers() {
        Elevator elevator = new Elevator(1, elevatorSubsystem);

        assertTimeout(
                java.time.Duration.ofSeconds(5),
                () -> elevator.timeToLoadPassengers(3)
        );
    }

    /**
     * Test the setMotorStatus method of the Elevator class.
     */
    @Test
    void testSetMotorStatus() {
        Elevator elevator = new Elevator(1, elevatorSubsystem);
        elevator.setMotorStatus(Elevator.MotorStatus.ON);
        assertEquals(Elevator.MotorStatus.ON, elevator.getMotorStatus());
    }

    /**
     * Test the setDoorStatus method of the Elevator class.
     */
    @Test
    void testSetDoorStatus() {
        Elevator elevator = new Elevator(1, elevatorSubsystem);
        elevator.setDoorStatus(Elevator.DoorStatus.OPEN);
        assertEquals(Elevator.DoorStatus.OPEN, elevator.getDoorStatus());
    }

    /**
     * Test the getCurrentFloor method of the Elevator class.
     */
    @Test
    void testGetCurrentFloor() {
        Elevator elevator = new Elevator(1, elevatorSubsystem);
        assertEquals(0, elevator.getCurrentFloor());
    }

    /**
     * Test the getElevatorId method of the Elevator class.
     */
    @Test
    void testGetElevatorId() {
        Elevator elevator = new Elevator(2, elevatorSubsystem);
        assertEquals(2, elevator.getElevatorId());
    }

    @Test
    void testAddRequestToElevatorQueue() {
        elevator = new Elevator(1, elevatorSubsystem);
        ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(),2, ElevatorRequest.ButtonDirection.UP, 4);
        ElevatorRequest elevatorRequest2 = new ElevatorRequest(LocalTime.now(), 2, ElevatorRequest.ButtonDirection.UP, 5);

        elevator.addRequestToElevatorQueue(elevatorRequest); // Add 1
        assertEquals(4, elevator.getElevatorQueue().peek().getButtonId()); // Check if 1 was added
        elevator.addRequestToElevatorQueue(elevatorRequest2); // Add 3
        assertEquals(2, elevator.getElevatorQueue().size()); // Check if size is now 2 

        // Try to add 2 (going up) when we are already going up to 3
        // We expect an error because we "missed" it
        ElevatorRequest elevatorRequest3 = new ElevatorRequest(LocalTime.now(), 3, ElevatorRequest.ButtonDirection.UP,2);
        assertThrows(AssertionError.class, () -> {
            elevator.addRequestToElevatorQueue(elevatorRequest3);
        });

        // Try to add 3 (going down) when we are on the way to 4
        // This should work because the user will have to wait for the elevator to head down to go to 3
        ElevatorRequest elevatorRequest4 = new ElevatorRequest(LocalTime.now(), 2, ElevatorRequest.ButtonDirection.DOWN,3);
        elevator.addRequestToElevatorQueue(elevatorRequest4); // Try to add 3 while going up to 4
        assertEquals(3, elevator.getElevatorQueue().size());

    }



    @Test
    void testRemoveRequestFromElevatorQueue() {
        elevator = new Elevator(1, elevatorSubsystem);
        ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 4,ElevatorRequest.ButtonDirection.UP, 1);
        ElevatorRequest elevatorRequest2 = new ElevatorRequest(LocalTime.now(), 5,ElevatorRequest.ButtonDirection.UP, 2);
        ElevatorRequest elevatorRequest3 = new ElevatorRequest(LocalTime.now(), 3, ElevatorRequest.ButtonDirection.UP, 3);
        elevator.addRequestToElevatorQueue(elevatorRequest);
        elevator.addRequestToElevatorQueue(elevatorRequest2);
        assertEquals(1, elevator.getElevatorQueue().peek().getButtonId());
        elevator.removeRequestFromElevatorQueue();
        assertEquals(2, elevator.getElevatorQueue().peek().getButtonId());
    }

    /**
     * Test Elevator state transitions
     */
    @Test
    void testElevatorStates() {
        elevator = new Elevator(1, elevatorSubsystem);
        elevator.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(elevator.getState(Elevator.State.IDLE), elevator.getCurrentState());
        ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 1,ElevatorRequest.ButtonDirection.UP, 2);
        elevator.addRequestToElevatorQueue(elevatorRequest);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(elevator.getState(Elevator.State.TRANSPORTING), elevator.getCurrentState());
    }

}


