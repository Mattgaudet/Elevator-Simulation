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
    /** Listener variable */
    private RequestProcessedListener listener;

    /** The elevators to schedule. */
    private Elevator[] elevatorCars = new Elevator[1]; // 1 elevator for now

    /** The schedule to receive requests from. */
    private Scheduler scheduler;

    /** The requests from the scheduler. */
    private ArrayList<ElevatorRequest> elevatorSubsystemRequestsQueue = new ArrayList<ElevatorRequest>();

    /** The responses from the elevators. */
    private ArrayList<ElevatorRequest> elevatorSubsystemResponseLog = new ArrayList<ElevatorRequest>();

    /**
     * Represents the state machine for the elevator subsystem.
     * Manages transitions between different states.
     */
    private ElevatorSubsystemStateMachine state = new ElevatorSubsystemStateMachine();

    /**
     * Set the listener for request processing.
     * @param listener The listener to set.
     */
    public void setListener(RequestProcessedListener listener) {
        this.listener = listener;
    }

    /**
     * Listener interface for notifying when a request is processed
     */
    public interface RequestProcessedListener {
        void onRequestProcessed();
    }

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
                switch (state.getCurrentState()) {
                    case IDLE:
                        handleIdleState();
                        break;
                    case PROCESSING:
                        handleStartMovingState();
                    case MOVING:
                        handleMovingState();
                        break;
                    case UNLOADING_FOR_PASSENGERS:
                        handleLoadingState();
                        break;

                }
            }
        }
    }

    /**
     * Handles the behavior when the elevator subsystem is in the MOVING state.
     * Removes a request from the scheduler, simulates elevator movement, and transitions to the next state.
     */
    private void handleMovingState() {
        synchronized (this.elevatorSubsystemRequestsQueue) {
            ElevatorRequest request = this.scheduler.getRequestQueueFromScheduler().remove(0); // Remove request
            // from queue
            this.elevatorSubsystemRequestsQueue.add(request);
            Log.print("(FORWARD) ElevatorSubsystem: Received ElevatorRequest(" + request + ") from Scheduler at "
                    + LocalTime.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.elevatorCars[0].addRequestToElevatorQueue(request);
            elevatorCars[0].simulateElevatorMovement();
            state.completeProcessing();
            state.transitionToUnloadPassengers();

            this.elevatorSubsystemRequestsQueue.notifyAll(); // Notify all threads waiting on task list

            this.scheduler.receiveRequestFromElevator(request);

            if (listener != null) {
                listener.onRequestProcessed();
            }
        }
    }

    /**
     * Handles the behavior when the elevator subsystem is in the IDLE state.
     */
    private void handleIdleState() {
        state.startProcessing();
    }

    /**
     * Handles the behavior when the elevator subsystem is in the MOVING state.
     */
    private void handleStartMovingState() {
        synchronized (this.scheduler.getRequestQueueFromScheduler()) {
            if (this.scheduler.getRequestQueueFromScheduler().isEmpty()) {
                try {
                    this.scheduler.getRequestQueueFromScheduler().wait(); // Wait for requests
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handleLoadingState();

        }
    }

    /**
     * Handles the behavior when the elevator subsystem is in the LOADING state.
     */
    private void handleLoadingState() {
        System.out.println("Loading...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Loading is done");
        state.transitionToMoving();
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