package Test;

import floor.ElevatorRequest;
import floor.Floor;
import floor.FloorSubsystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FloorSubsystemTest {
    private static int counter;
    private static FloorSubsystem floorSubsystem;
    public FloorSubsystemTest(){}
    @BeforeAll
    public static void setUp(){
        floorSubsystem = null;
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
    void addInAndRemoveOut() {
        floorSubsystem = new FloorSubsystem();
        ElevatorRequest buttonPress = new ElevatorRequest(ElevatorRequest.ButtonDirection.UP, 1, 1, null);

        floorSubsystem.addIn(buttonPress);
        assertEquals(1, floorSubsystem.getAllElevatorRequestsfromFloorSubsystem().size());

        floorSubsystem.removeOut(0);
        assertTrue(floorSubsystem.getAllElevatorRequestsfromFloorSubsystem().isEmpty());
    }

    @Test
    void changeLampStatus() {
        floorSubsystem = new FloorSubsystem();
        floorSubsystem.changeLampStatus(ElevatorRequest.ButtonDirection.UP);

        for (Floor floor : floorSubsystem.floorArray) {
            assertTrue(floor.isUpLampOn);
            assertFalse(floor.isDownLampOn);
        }
    }
}
