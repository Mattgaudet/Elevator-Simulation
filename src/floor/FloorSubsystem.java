package floor;

import log.Log;

import scheduler.Scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import floor.ElevatorRequest.ButtonDirection;

public class FloorSubsystem implements Runnable {
    private final Scheduler scheduler;
    private final List<ElevatorRequest> elevatorRequests;
    private final Floor[] floorArray;
    private ButtonDirection direction;

    public FloorSubsystem() {
        scheduler = new Scheduler(this);
        elevatorRequests = new ArrayList<>();
        floorArray = new Floor[0];

    }

    public void addIn(ElevatorRequest elevatorRequest) {
        elevatorRequests.add(elevatorRequest);
    }

    // for iter 1
    public void receiveRequestFromScheduler(ElevatorRequest elevatorRequest) {
        
        Log.print("(BACK) FloorSubsystem: Received ElevatorRequest(" + elevatorRequest + ") BACK from Scheduler at "
                + LocalTime.now());
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        elevatorRequests.add(elevatorRequest);
    }

    public void removeOut(int index) {
        elevatorRequests.remove(index);
    }

    public List<ElevatorRequest> getAllElevatorRequestsFromFloorSubsystem() {
        return elevatorRequests;
    }

    public void changeLampStatus(ButtonDirection direction) {
        for (Floor floor : floorArray) {
            floor.changeLampStatus(direction);
        }
    }

    @Override
    public void run() {
        CSVParser parser = new CSVParser();
        List<ElevatorRequest> elevatorRequestList = parser.parseCSV("res/input.txt");

        // Add request to list
        elevatorRequests.addAll(elevatorRequestList);

        // Notify all threads once the file has been read
        synchronized (elevatorRequests) {
                elevatorRequests.notifyAll();
        }

    }
}