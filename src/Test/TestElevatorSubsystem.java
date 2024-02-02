package Test;
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

class TestElevatorSubsystem {
    private static int counter;
    private static ElevatorSubsystem elevatorSubsystem;
    public TestElevatorSubsystem(){}
    @BeforeAll
    public static void setUp(){
        elevatorSubsystem = null;
        counter = 0;
    }

    @AfterAll
    public static void tearDown(){
        System.out.println("All tests are done");
    }
    @AfterEach
    public void summery(){
        counter++;
        System.out.println("Number of tests run: "+ counter);
    }


    @Test
    void testElevatorSubsystemInitialization() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        Scheduler scheduler = new Scheduler(floorSubsystem);
        elevatorSubsystem = new ElevatorSubsystem(scheduler);

        assertNotNull(elevatorSubsystem);
        assertNotNull(elevatorSubsystem.getElevatorSubsystemRequestsQueue());
        assertEquals(0, elevatorSubsystem.getElevatorSubsystemRequestsQueue().size());
    }

    @Test
    void testElevatorSubsystemRun() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        Scheduler scheduler = new Scheduler(floorSubsystem);
        elevatorSubsystem = new ElevatorSubsystem(scheduler);

        Thread elevatorSubsystemThread = new Thread(elevatorSubsystem);
        elevatorSubsystemThread.start();

        ElevatorRequest request = new ElevatorRequest(ButtonDirection.UP,1,2, LocalTime.now());
        scheduler.addToRequestQueue(request);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(1, elevatorSubsystem.getElevatorSubsystemRequestsQueue().size());
    }

    @Test
    void testAddResponseList() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        Scheduler scheduler = new Scheduler(floorSubsystem);
        elevatorSubsystem = new ElevatorSubsystem(scheduler);

        ElevatorRequest response = new ElevatorRequest(ButtonDirection.UP,1,2, LocalTime.now());
        elevatorSubsystem.addResponseList(response);

        assertEquals(1, elevatorSubsystem.elevatorSubsystemResponseLog.size());
        assertEquals(response, elevatorSubsystem.elevatorSubsystemResponseLog.get(0));
    }

}

