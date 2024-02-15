package scheduler;

import config.ProcessingHandler;

public class SchedulerStateMachine implements ProcessingHandler {
    /**
     * Enumeration representing possible states of the scheduler subsystem.
     */
    public enum SchedulerState {
        IDLE,
        PROCESSING
    }

    private SchedulerState currentState;

    /**
     * Initializes the scheduler subsystem state machine in the IDLE state and prints an initialization message.
     */
    public SchedulerStateMachine() {
        this.currentState = SchedulerState.IDLE;
        System.out.println("Scheduler Subsystem State Machine initialized in state: " + currentState);
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
    public synchronized void startProcessing() {
        if (currentState == SchedulerState.IDLE) {
            currentState = SchedulerState.PROCESSING;
        } else {
            throw new IllegalStateException("Invalid state transition to start processing");
        }
    }

    /**
     * Completes the processing if the current state is PROCESSING; otherwise, throws an IllegalStateException.
     */
    public synchronized void completeProcessing() {
        if (currentState == SchedulerState.PROCESSING) {
            currentState = SchedulerState.IDLE;
        } else {
            throw new IllegalStateException("Invalid state transition to complete processing");
        }
    }
}




