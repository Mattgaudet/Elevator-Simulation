package elevator;

import floor.ElevatorRequest.ButtonDirection;
import floor.ElevatorRequest;
import java.time.LocalTime;
import java.util.ArrayList;
import log.Log;
import scheduler.Scheduler;

/**
 * The elevator system. Manages the scheduling of the elevators.
 */
public class ElevatorSubsystem implements Runnable {

    /** The elevators to schedule. */
    private Elevator[] elevatorCars = new Elevator[1]; // 1 elevator for now

    /** The schedule to receive requests from. */
    private Scheduler scheduler;

    /** The requests from the scheduler. */
    private ArrayList<ElevatorRequest> elevatorSubsystemRequestsQueue = new ArrayList<ElevatorRequest>();

    /** The responses from the elevators. */
    private ArrayList<ElevatorRequest> elevatorSubsystemResponseLog = new ArrayList<ElevatorRequest>();

    /**
     * Create a new elevator subsystem. Creates only 1 elevator for now.
     * @param scheduler The scheduler to use for requests.
     */
    public ElevatorSubsystem(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.elevatorCars[0] = new Elevator(0);
    }

    /**
     * The entrypoint of the system. Pulls messages from the scheduler, forwards
     * them to the elevators, and sends them back to the scheduler.
     */
    public void run() {
        synchronized (this.scheduler.getRequestQueueFromScheduler()) {

            while (true) {
                if (this.scheduler.getRequestQueueFromScheduler().isEmpty()) {
                    try {
                        this.scheduler.getRequestQueueFromScheduler().wait(); // Wait for requests
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                synchronized (this.elevatorSubsystemRequestsQueue) {
                    ElevatorRequest request = this.scheduler.getRequestQueueFromScheduler().remove(0); // Remove request
                                                                                                       // from queue
                    this.elevatorSubsystemRequestsQueue.add(request); // Add request to task list
                    this.elevatorCars[0].addRequestToElevatorQueue(request);
                    Log.print("(FORWARD) ElevatorSubsystem: Received ElevatorRequest(" + request + ") from Scheduler at "
                            + LocalTime.now());
                    // TODO: Replace print statement with move the elevator according to set logic
                    // To be removed (for debug only)
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.elevatorSubsystemRequestsQueue.notifyAll(); // Notify all threads waiting on task list

                    // After processing the request, send it back to the scheduler - Iter 1
                    // (back and forth communication between FloorSubsystem <- Scheduler <- ElevatorSubsystem)
                    // TODO: remove request from elevator when processed elevatorCars[0].removeRequestFromElevatorQueue();
                    // TODO: update the status of the elevator, and pass it to the scheduler
                    this.scheduler.receiveRequestFromElevator(request);

                }
            }
        }
    }

    /**
     * Get the requests from the scheduler for testing.
     * @return The requests from the scheduler for testing.
     */
    public ArrayList<ElevatorRequest> getElevatorSubsystemRequestsQueue() {
        return this.elevatorSubsystemRequestsQueue;
    }

    /**
     * Get the requests from the elevators for testing.
     * @return The requests from the elevators for testing.
     */
    public ArrayList<ElevatorRequest> getElevatorSubsystemResponseLog() {
        return this.elevatorSubsystemResponseLog;
    }

    /**
     * Add an elevator response.
     * @param elevatorRequest The elevator response.
     */
    public void addResponseList(ElevatorRequest elevatorRequest) {
        synchronized (this.elevatorSubsystemResponseLog) {
            this.elevatorSubsystemResponseLog.add(elevatorRequest); // Add response to response list
            this.elevatorSubsystemResponseLog.notifyAll(); // Notify all threads waiting on response list
        }
    }

    /**
     * Change the lamp status to indicate a request.
     * @param direction The direction of the request.
     */
    public void changeLampStatus(ButtonDirection direction) {
        this.scheduler.changeLampStatus(direction); // Change lamp status in scheduler
    }
}
