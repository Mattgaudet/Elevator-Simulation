package Test;

import floor.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scheduler.Scheduler;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchedulerTest {
    private static int counter;
    private static Scheduler scheduler;
    public SchedulerTest(){}
    @BeforeAll
    public static void setUp(){
        scheduler = null;
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
    void addToRequestQueue() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        scheduler = new Scheduler(floorSubsystem);
        ElevatorRequest elevatorRequest = new ElevatorRequest(ElevatorRequest.ButtonDirection.UP, 3, 4, LocalTime.now());

        scheduler.addToRequestQueue(elevatorRequest);
        ArrayList<ElevatorRequest> requestQueue = scheduler.getRequestQueuefromScheduler();

        assertTrue(requestQueue.contains(elevatorRequest));
    }

    @Test
    void run() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        scheduler = new Scheduler(floorSubsystem);
        ElevatorRequest elevatorRequest = new ElevatorRequest(ElevatorRequest.ButtonDirection.UP, 3, 4, LocalTime.now());


        floorSubsystem.addIn(elevatorRequest);
        Thread schedulerThread = new Thread(scheduler);  // Start the scheduler in a separate thread
        schedulerThread.start();

        try {
            Thread.sleep(1000); // Wait for a short time to allow the scheduler to process the request
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<ElevatorRequest> requestQueue = scheduler.getRequestQueuefromScheduler(); // Check if the request is in the request queue
        assertTrue(requestQueue.contains(elevatorRequest));
    }

    @Test
    void addToResponseLog() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        scheduler = new Scheduler(floorSubsystem);
        ElevatorRequest elevatorRequest = new ElevatorRequest(ElevatorRequest.ButtonDirection.UP, 3, 4, LocalTime.now());

        scheduler.addToResponseLog(elevatorRequest);
        ArrayList<ElevatorRequest> responseLog = scheduler.getSchedularResponseLog();

        assertTrue(responseLog.contains(elevatorRequest));
    }
}
