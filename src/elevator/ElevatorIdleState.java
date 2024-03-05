package elevator;

import common.Log;

/**
 * Elevator is stationary and waits for requests to be added to the elevatorQueue
 */
public class ElevatorIdleState implements ElevatorState{
    /**
     * Waits for a request to be added to the elevator's queue, then transitions to Transporting state once there is
     * a request
     * @param elevator The elevator context
     */
    @Override
    public void action(Elevator elevator) {
        Log.print("Elevator " + elevator.getElevatorId() + " transitioned to IDLE state");
        synchronized (elevator.getQueueLock()) {
            while (elevator.getElevatorQueue().isEmpty()) {
                try {
                    elevator.getQueueLock().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        elevator.setState(Elevator.State.TRANSPORTING);
    }
}
