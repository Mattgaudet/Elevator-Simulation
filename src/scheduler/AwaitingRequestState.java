package scheduler;

import common.Log;
import floor.ElevatorRequest;

/**
 * In this state the scheduler waits to receive new requests from the FloorSubsystem
 */
public class AwaitingRequestState implements SchedulerState {
    /** the scheduler */
    private Scheduler scheduler;

    /**
     * Constructor for the state
     * @param scheduler the scheduler
     */
    public AwaitingRequestState(Scheduler scheduler) {
        this.scheduler = scheduler;
        Log.print("Scheduler: State transitioned to AWAITING REQUEST STATE.");
    }

    /**
     * Transition to ProcessingRequestState
     * @param scheduler the scheduler
     * @param requestData the request data
     */
    @Override
    public void processRequest(Scheduler scheduler, byte[] requestData) {
        scheduler.setState(new ProcessingRequestState());
        Log.print("Scheduler: State transitioned to PROCESSING REQUEST STATE.");
        scheduler.state.processRequest(scheduler, requestData);
    }

    @Override
    public void processRequest(Scheduler scheduler, ElevatorRequest request, int elevatorID) {
        // Not needed
    }
}