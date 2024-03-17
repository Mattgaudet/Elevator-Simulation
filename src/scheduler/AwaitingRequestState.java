package scheduler;

import common.Log;
import floor.ElevatorRequest;

public class AwaitingRequestState implements SchedulerState {
    private Scheduler scheduler;

    public AwaitingRequestState(Scheduler scheduler) {
        this.scheduler = scheduler;
        Log.print("Scheduler: State transitioned to AWAITING REQUEST STATE.");
    }

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