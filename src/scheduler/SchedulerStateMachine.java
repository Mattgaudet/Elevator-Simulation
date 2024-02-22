package scheduler;

/**
 * Represents the state of the scheduler.
 */
public class SchedulerStateMachine {
    /**
     * Enumeration representing possible states of the scheduler subsystem.
     */
    public enum SchedulerState {
        IDLE,
        PROCESSING
    }

    /** The current state of the scheduler. */
    private SchedulerState currentState;

    /**
     * Initializes the scheduler subsystem state machine in the IDLE state and prints an initialization message.
     */
    public SchedulerStateMachine() {
        startIdling();
    }

    /**
     * Gets the current state of the scheduler subsystem.
     *
     * @return The current state of the scheduler subsystem.
     */
    public SchedulerState getCurrentState() {
        return currentState;
    }

    /**
     * Starts the processing if the current state is IDLE; otherwise, throws an IllegalStateException.
     */
    public void startProcessing() {
        currentState = SchedulerState.PROCESSING;
    }

    /**
     * Completes the processing if the current state is PROCESSING; otherwise, throws an IllegalStateException.
     */
    public void startIdling() {
        currentState = SchedulerState.IDLE;
    }
}
