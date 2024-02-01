package floor;

import floor.ElevatorRequest.ButtonDirection;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import log.Log;

/**
 * Create a new floor subsystem to manage the floors.
 */
public class FloorSubsystem implements Runnable {

    /** The requests from the CSV file. */
    private final List<ElevatorRequest> elevatorRequests;

    /** The array of floors. */
    private final Floor[] floorArray;

    /**
     * Create a new floor subsystem.
     */
    public FloorSubsystem() {
        elevatorRequests = new ArrayList<>();
        floorArray = new Floor[0];

    }

    /**
     * Add a new elevator request.
     * @param elevatorRequest The elevator request.
     */
    public void addIn(ElevatorRequest elevatorRequest) {
        elevatorRequests.add(elevatorRequest);
    }

    /**
     * Get a response from the scheduler.
     * @param elevatorRequest The response as an elevator request.
     */
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

    /**
     * Remove an elevator request at a specific index.
     * @param index The index to remove at.
     */
    public void removeOut(int index) {
        elevatorRequests.remove(index);
    }

    /**
     * Get all of the elevator requests.
     * @return All of the elevator requests.
     */
    public List<ElevatorRequest> getAllElevatorRequestsFromFloorSubsystem() {
        return elevatorRequests;
    }

    /**
     * Change the directional status of the lamp for all floors.
     * @param direction The directional status of the lamp for all floors.
     */
    public void changeLampStatus(ButtonDirection direction) {
        for (Floor floor : floorArray) {
            floor.changeLampStatus(direction);
        }
    }

    /**
     * The entrypoint of the system. Creates a CSV file and sends requests to the scheduler.
     */
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