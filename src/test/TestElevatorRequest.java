package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

import floor.ElevatorRequest;
import floor.ElevatorRequest.ButtonDirection;
import static test.Misc.assertWait;

public class TestElevatorRequest {

    @Test
    public void testCompareByTime()
    {
        ArrayList<ElevatorRequest> ers = new ArrayList<>();
        ers.add(new ElevatorRequest(LocalTime.of(3, 0), 1, ButtonDirection.NONE, 0));
        ers.add(new ElevatorRequest(LocalTime.of(9, 0), 2, ButtonDirection.NONE, 0));
        ers.add(new ElevatorRequest(LocalTime.of(1, 0), 3, ButtonDirection.NONE, 0));
        ers.add(new ElevatorRequest(LocalTime.of(5, 0), 4, ButtonDirection.NONE, 0));
        ers.add(new ElevatorRequest(LocalTime.of(3, 5), 5, ButtonDirection.NONE, 0));

        Collections.sort(ers, (e1, e2) -> e1.compareByTime(e2));

        assertEquals(3, ers.get(0).getFloorNumber());
        assertEquals(1, ers.get(1).getFloorNumber());
        assertEquals(5, ers.get(2).getFloorNumber());
        assertEquals(4, ers.get(3).getFloorNumber());
        assertEquals(2, ers.get(4).getFloorNumber());
    }

    @Test
    public void testWaitByTime() {
        LocalTime baseline = LocalTime.of(0, 0, 0);
        ElevatorRequest er1 = new ElevatorRequest(LocalTime.of(0, 0, 0), 1, ButtonDirection.NONE, 0);
        ElevatorRequest er2 = new ElevatorRequest(LocalTime.of(0, 0, 1), 2, ButtonDirection.NONE, 0);
        ElevatorRequest er3 = new ElevatorRequest(LocalTime.of(0, 0, 3), 3, ButtonDirection.NONE, 0);
        ElevatorRequest er4 = new ElevatorRequest(LocalTime.of(0, 0, 5), 4, ButtonDirection.NONE, 0);
        ElevatorRequest er5 = new ElevatorRequest(LocalTime.of(0, 0, 9), 5, ButtonDirection.NONE, 0);

        assertWait(() -> er1.waitForTime(baseline), 0, 1);
        assertWait(() -> er2.waitForTime(baseline), 1, 2);
        assertWait(() -> er3.waitForTime(baseline), 3, 4);
        assertWait(() -> er4.waitForTime(baseline), 5, 6);
        assertWait(() -> er5.waitForTime(baseline), 9, 10);
    }
}
