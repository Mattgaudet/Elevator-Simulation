package Test;

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

public class TestFloor {

    private static int counter;
    private static Floor floor;
    public TestFloor(){}
    @BeforeAll
    public static void setUp(){
        floor = null;
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
    void getFloorNumber() {
        Scheduler scheduler = new Scheduler(new FloorSubsystem());
        floor = new Floor(1, scheduler);
        assertEquals(1, floor.getFloorNumber());
    }

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
