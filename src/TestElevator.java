package test;

import static org.junit.jupiter.api.Assertions.*;

import elevator.Elevator;
import floor.ElevatorRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

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
    public void summery() {
        counter++;
        System.out.println("Number of tests run: " + counter);
    }

    /**
     * Test the simulateElevatorMovement method of the Elevator class.
     */
    @Test
    void testSimulateElevatorMovement() {
        elevator = new Elevator(1);
        ElevatorRequest elevatorRequest = new ElevatorRequest(ElevatorRequest.ButtonDirection.UP, 3, 4, LocalTime.now());
        elevator.simulateElevatorMovement(elevatorRequest);
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
        assertEquals(2000, elevator.findTravelTime(0, 4));
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
}


