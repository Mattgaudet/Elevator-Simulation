package elevator;

import config.ProcessingHandler;
import log.Log;

public class ElevatorSubsystemStateMachine implements ProcessingHandler {
    private ElevatorSubsystemState currentState;

    /**
     * Enumeration representing possible states of the elevator subsystem.
     */
    public enum ElevatorSubsystemState {
        IDLE,
        TRANSPORTING,
        PROCESSING
    }

    /**
     * Resets the elevator subsystem to the IDLE state and prints a corresponding message.
     */
    @Override
    public void completeProcessing() {
        currentState = ElevatorSubsystemState.IDLE;
        Log.print("Elevator Subsystem: Reset to IDLE state");
    }

    /**
     * Starts the processing if the current state is IDLE; otherwise, throws an IllegalStateException.
     */
    @Override
    public void startProcessing() {
        if (currentState == ElevatorSubsystemState.IDLE) {
            transitionToProcessing();
        } else {
            throw new IllegalStateException("Invalid state transition to start processing");
        }
    }

    /**
     * Transitions the elevator subsystem to the PROCESSING state and prints a corresponding message.
     */
    private void transitionToProcessing() {
        currentState = ElevatorSubsystemState.PROCESSING;
        Log.print("Elevator Subsystem transitioned to Processing requests state.");
    }

    /**
     * Initializes the elevator subsystem state machine in the IDLE state and prints an initialization message.
     */
    public ElevatorSubsystemStateMachine() {
        currentState = ElevatorSubsystemState.IDLE;
        Log.print("Elevator Subsystem State Machine initialized in state: " + currentState);
    }

    /**
     * Gets the current state of the elevator subsystem.
     *
     * @return The current state of the elevator subsystem.
     */
    public ElevatorSubsystemState getCurrentState() {
        return currentState;
    }

    /**
     * Transitions the elevator subsystem to the MOVING state and prints a corresponding message.
     */
    public void transitionToTransporting() {
        currentState = ElevatorSubsystemState.TRANSPORTING;
        Log.print("Elevator Subsystem: Transitioned to TRANSPORTING state. En route to destination.");
    }
}




