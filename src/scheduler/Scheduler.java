package scheduler;

import floor.ElevatorRequest.ButtonDirection;
import floor.ElevatorRequest;
import floor.FloorSubsystem;
import java.time.LocalTime;
import java.util.ArrayList;
import common.Log;

/**
 * Communicates between the ElevatorSubsystem and the FloorSubsystem. The Scheduler
 * notifies the Elevator when there is work to be done. After data is sent to the Elevator,
 * it forwards the data to the Floor.
 */
public class Scheduler implements Runnable {
    /** The requests to forward to the elevator subsystem. */
    private ArrayList<ElevatorRequest> schedulerRequestsQueue = new ArrayList<>();

    /** The requests to forward to the floor subsystem. */
    private ArrayList<ElevatorRequest> schedulerResponseLog = new ArrayList<>();

    /** The floor subsystem. */
    private FloorSubsystem floorSubsystem;
    private SchedulerStateMachine stateMachine = new SchedulerStateMachine();

    /**
     * Create a new scheduler.
     * @param floorSubsystem The floor subsystem for the scheduler.
     */
    public Scheduler(FloorSubsystem floorSubsystem) {
        this.floorSubsystem = floorSubsystem;
    }

    /**
     * Create a new scheduler with a specified queue of requests.
     * @param floorSubsystem The floor subsystem for the scheduler.
     * @param requestsQueue The queue of requests.
     */
    public Scheduler(FloorSubsystem floorSubsystem, ArrayList<ElevatorRequest> requestsQueue) {
        this.floorSubsystem = floorSubsystem;
        this.schedulerRequestsQueue = requestsQueue;
    }

    /**
     * Get the queue of requests.
     * @return The queue of requests.
     */
    public ArrayList<ElevatorRequest> getRequestQueueFromScheduler() {
        return schedulerRequestsQueue;
    }

    /**
     * Get the responses.
     * @return The responses.
     */
    public ArrayList<ElevatorRequest> getSchedulerResponseLog() {
        return schedulerResponseLog;
    }

    /**
     * Add a request to the scheduler.
     * @param elevatorRequest The request to add to the scheduler.
     */
    public void addToRequestQueue(ElevatorRequest elevatorRequest) {
        synchronized (schedulerRequestsQueue) {
            schedulerRequestsQueue.add(elevatorRequest);
            Log.print("(FORWARD) Added elevator request " + elevatorRequest + " to request queue");
            schedulerRequestsQueue.notifyAll();
        }
    }

    /**
     * Get a request from an elevator and send the the floor subsystem.
     * @param request The request from an elevator.
     */
    public synchronized void receiveRequestFromElevator(ElevatorRequest request) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.print("(BACK) Scheduler: Received ElevatorRequest(" + request + ") BACK from ElevatorSubsystem at "
                + LocalTime.now() + ".");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (schedulerRequestsQueue) {
            // send the request to the floorSubsystem
            floorSubsystem.receiveRequestFromScheduler(request);
            schedulerRequestsQueue.remove(request); //remove from queue
            schedulerRequestsQueue.notifyAll(); // Notify any threads that are waiting for new requests
        }
    }

    /**
     * The entrypoint of the scheduler. Continuously checks if there are any jobs in the
     * FloorSubsystem. If there are, it adds the jobs to the queue and removes it from the
     * FloorSubsystem.
     */
    @Override
    public void run() {
        while (true) {

        }
    }

    /**
     * Handles the processing state of the scheduler. Processes elevator requests from the FloorSubsystem.
     */
    private void handleProcessingState() {
        // TODO: Ali
        stateMachine.startIdling();
    }

    /**
     * Handles the idle state of the scheduler. Starts processing if the current state is IDLE.
     */
    private void handleIdleState() {
        // TODO: Ali
        stateMachine.startProcessing();
    }

    /**
     * Add an elevator request to the responses.
     * @param elevatorRequest The elevator request to add.
     */
    public void addToResponseLog(ElevatorRequest elevatorRequest) {
        synchronized (schedulerResponseLog) {
            schedulerResponseLog.add(elevatorRequest);
            schedulerResponseLog.notifyAll();
        }
    }

    /**
     * Change the lamp directional status of the floor subsystem.
     * @param direction The lamp directional status.
     */
    public void changeLampStatus(ButtonDirection direction) {
        floorSubsystem.changeLampStatus(direction);
    }
}