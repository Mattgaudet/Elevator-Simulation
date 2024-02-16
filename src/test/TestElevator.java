package test;

import static org.junit.jupiter.api.Assertions.*;

import elevator.Elevator;
import floor.ElevatorRequest;
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

    /**
     * Set up initial conditions before running the tests.
     */
    @BeforeAll
    public static void setUp() {
        elevator = null;
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
     * Test the simulateElevatorMovement method of the Elevator class.
     */
    @Test
    void testSimulateElevatorMovement() {
        elevator = new Elevator(1);
        ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 3, ElevatorRequest.ButtonDirection.UP, 4);
        elevator.addRequestToElevatorQueue(elevatorRequest);
        elevator.simulateElevatorMovement();
        assertEquals(4, elevator.getCurrentFloor());
    }

    /**
     * Test the arrivedFloor method of the Elevator class.
     */
    @Test
    void testArrivedFloor() {
        elevator = new Elevator(1);
        assertEquals(-1, elevator.arrivedFloor(3));
        assertEquals(3, elevator.getCurrentFloor());
    }

    /**
     * Test the findTravelTime method of the Elevator class.
     */
    @Test
    void testFindTravelTime() {
        elevator = new Elevator(1);
        assertEquals(2000, elevator.findTravelTime(4));
    }

    /**
     * Test the timeToLoadPassengers method of the Elevator class.
     */
    @Test
    void testTimeToLoadPassengers() {
        Elevator elevator = new Elevator(1);

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
        Elevator elevator = new Elevator(1);
        elevator.setMotorStatus(Elevator.MotorStatus.ON);
        assertEquals(Elevator.MotorStatus.ON, elevator.getMotorStatus());
    }

    /**
     * Test the setDoorStatus method of the Elevator class.
     */
    @Test
    void testSetDoorStatus() {
        Elevator elevator = new Elevator(1);
        elevator.setDoorStatus(Elevator.DoorStatus.OPEN);
        assertEquals(Elevator.DoorStatus.OPEN, elevator.getDoorStatus());
    }

    /**
     * Test the getCurrentFloor method of the Elevator class.
     */
    @Test
    void testGetCurrentFloor() {
        Elevator elevator = new Elevator(1);
        assertEquals(0, elevator.getCurrentFloor());
    }

    /**
     * Test the getElevatorId method of the Elevator class.
     */
    @Test
    void testGetElevatorId() {
        Elevator elevator = new Elevator(2);
        assertEquals(2, elevator.getElevatorId());
    }

    @Test
    void testAddRequestToElevatorQueue() {
        elevator = new Elevator(1);
        ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(),4, ElevatorRequest.ButtonDirection.UP, 2);
        ElevatorRequest elevatorRequest2 = new ElevatorRequest(LocalTime.now(), 5, ElevatorRequest.ButtonDirection.UP, 2);

        elevator.addRequestToElevatorQueue(elevatorRequest); // Add 5
        assertEquals(4, elevator.getElevatorQueue().peek()); // Check if 4 was added
        elevator.addRequestToElevatorQueue(elevatorRequest2); // Add 5
        assertEquals(2, elevator.getElevatorQueue().size()); // Check if 5 was added

        // Try to add 3 (going up) when we are already going up to 4
        // We expect an error because we "missed" it
        ElevatorRequest elevatorRequest3 = new ElevatorRequest(LocalTime.now(), 3, ElevatorRequest.ButtonDirection.UP,2);
        assertThrows(AssertionError.class, () -> {
            elevator.addRequestToElevatorQueue(elevatorRequest3);
        });

        // Try to add 3 (going down) when we are on the way to 4
        // This should work because the user will have to wait for the elevator to head down to go to 3
        ElevatorRequest elevatorRequest4 = new ElevatorRequest(LocalTime.now(), 3, ElevatorRequest.ButtonDirection.DOWN,2);
        elevator.addRequestToElevatorQueue(elevatorRequest4); // Try to add 3 while going up to 4
        assertEquals(3, elevator.getElevatorQueue().size());

    }

    @Test
    void testRemoveRequestFromElevatorQueue() {
        elevator = new Elevator(1);
        ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 4,ElevatorRequest.ButtonDirection.UP, 2);
        ElevatorRequest elevatorRequest2 = new ElevatorRequest(LocalTime.now(), 5,ElevatorRequest.ButtonDirection.UP, 2);
        ElevatorRequest elevatorRequest3 = new ElevatorRequest(LocalTime.now(), 3, ElevatorRequest.ButtonDirection.UP, 2);
        elevator.addRequestToElevatorQueue(elevatorRequest);
        elevator.addRequestToElevatorQueue(elevatorRequest2);
        assertEquals(4, elevator.getElevatorQueue().peek());
        elevator.removeRequestFromElevatorQueue();
        assertEquals(5, elevator.getElevatorQueue().peek());
    }
}


