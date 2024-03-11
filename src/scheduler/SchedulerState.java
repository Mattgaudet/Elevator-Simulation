package scheduler;

import floor.ElevatorRequest;

public interface SchedulerState {
    void processRequest(Scheduler scheduler, byte[] requestData);
    void processRequest(Scheduler scheduler, ElevatorRequest request, int elevatorID);
}