package test;

import static org.junit.jupiter.api.Assertions.*;

import common.Config;
import common.Log;
import elevator.Elevator;
import elevator.ElevatorFaultState;
import elevator.ElevatorSubsystem;
import floor.CSVParser;
import floor.ElevatorRequest;
import scheduler.ElevatorDispatchState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;


/**
 * JUnit tests for the Faults.
 */
public class TestFaults {
    private static int counter;
    private static Elevator elevator;
    private static ElevatorSubsystem elevatorSubsystem;
    private static Thread elevatorSubsystemThread;
    private static ElevatorDispatchState elevatorDispatchState;

    /**
     * Set up initial conditions before running the tests.
     */
    @BeforeAll
    public static void setUp() {
        elevator = null;
        counter = 0;
        elevatorDispatchState = new ElevatorDispatchState();
        elevatorSubsystem = new ElevatorSubsystem(3);
        elevatorSubsystemThread = new Thread(elevatorSubsystem);
        elevatorSubsystemThread.start();
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
     * Unit test for BAD_REQUEST transient fault - ElevatorSubsystem identifies the fault and does not assign the
     * request to an elevator
     */
    @Test
    void testBadRequestFault() {
        ElevatorRequest er = new ElevatorRequest(LocalTime.now(), 0,ElevatorRequest.ButtonDirection.UP, 2);
        er.addFault(CSVParser.ElevatorFault.BAD_REQUEST);
        elevatorDispatchState = new ElevatorDispatchState();
        elevatorDispatchState.sendRequestToElevator(er, 0);
        //assert that the request is not assigned to the elevator
        assertEquals(0, elevatorSubsystem.getElevatorCars()[0].getElevatorQueue().size());
    }

    /**
     * Unit test for BAD_REQUEST transient fault - ElevatorSubsystem identifies the fault and does not assign the
     * request to an elevator
     */
    @Test
    void testDeathFault() {
        ElevatorRequest er = new ElevatorRequest(LocalTime.now(), 0,ElevatorRequest.ButtonDirection.UP, 2);
        er.addFault(CSVParser.ElevatorFault.DEATH);
        elevatorDispatchState.sendRequestToElevator(er, 1);
        try {
            Thread.sleep(3000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        //assert that the elevator is in fault state
        assertTrue(elevatorSubsystem.getElevatorCars()[1].getCurrentState() instanceof ElevatorFaultState);
    }

    /**
     * Test elevator timout fault using a Thread to edit the start time to simulate a timeout fault
     */
    @Test
    void simulateTimeoutFault() {
        class testThread extends Thread{
            public void run() {
                try {
                    Thread.sleep(12000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                Log.print(">> testThread: editing elapsedTime");
                elevatorSubsystem.getElevatorCars()[2].changeTime();;
            }
        }
        ElevatorRequest er = new ElevatorRequest(LocalTime.now(), 0,ElevatorRequest.ButtonDirection.UP, 5);
        elevatorSubsystem.assignRequest(er, 2);
        testThread th = new testThread();
        th.start();
        try {
            Thread.sleep(20000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(elevatorSubsystem.getElevatorCars()[2].getCurrentState() instanceof ElevatorFaultState);
    }

    /**
     * Unit test for DOOR_NOT_CLOSE transient fault
     */
    @Test
    void testDoorNotCloseFault() {
        Elevator elv = new Elevator(2, elevatorSubsystem);
        elv.start();

        ElevatorRequest er = new ElevatorRequest(LocalTime.now(), 0,ElevatorRequest.ButtonDirection.UP, 2);
        er.addFault(CSVParser.ElevatorFault.DOOR_NOT_CLOSE);
        elv.addRequestToElevatorQueue(er);
        assertEquals(Elevator.DoorStatus.CLOSED, elv.getDoorStatus()); //doors initially closed
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Elevator.DoorStatus.OPEN, elv.getDoorStatus()); //doors opened on for loading on floor 0
        //fault is processed in the loadElevator method in ElevatorTransportingState
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Elevator.DoorStatus.CLOSED, elv.getDoorStatus()); //doors closed after
        assertEquals("NO_FAULT", er.getFault()); //fault is removed after
    }

    /**
     * Unit test for DOOR_NOT_OPEN transient fault
     */
    @Test
    void testDoorNotOpenFault() {
        elevator = new Elevator(1, elevatorSubsystem);
        elevator.start();

        ElevatorRequest er = new ElevatorRequest(LocalTime.now(), 0,ElevatorRequest.ButtonDirection.UP, 2);
        er.addFault(CSVParser.ElevatorFault.DOOR_NOT_OPEN);
        elevator.addRequestToElevatorQueue(er);
        assertEquals(Elevator.DoorStatus.CLOSED, elevator.getDoorStatus()); //doors initially closed
        try {
            Thread.sleep(Config.DOOR_TIME + Config.LOAD_TIME + 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Elevator.DoorStatus.OPEN, elevator.getDoorStatus()); //doors opened on for loading on floor 0
        //fault is processed in the loadElevator method in ElevatorTransportingState
        try {
            Thread.sleep(Config.DOOR_TIME + Config.LOAD_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Elevator.DoorStatus.CLOSED, elevator.getDoorStatus()); //doors closed after
        assertEquals("NO_FAULT", er.getFault()); //fault is removed after, operation continues
    }


}

