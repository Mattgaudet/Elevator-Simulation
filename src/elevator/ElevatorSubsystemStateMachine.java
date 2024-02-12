package elevator;

import config.ProcessingHandler;

public class ElevatorSubsystemStateMachine implements ProcessingHandler {
    private ElevatorSubsystemState currentState;

    /**
     * Enumeration representing possible states of the elevator subsystem.
     */
    public enum ElevatorSubsystemState {
        IDLE,
        MOVING,
        PROCESSING, UNLOADING_FOR_PASSENGERS
    }

    /**
     * Resets the elevator subsystem to the IDLE state and prints a corresponding message.
     */
    @Override
    public void completeProcessing() {
        currentState = ElevatorSubsystemState.IDLE;
        System.out.println("Elevator Subsystem reset to IDLE state and stopped! ");
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
        System.out.println("Elevator Subsystem transitioned to Processing requests state.");
    }

    /**
     * Initializes the elevator subsystem state machine in the IDLE state and prints an initialization message.
     */
    public ElevatorSubsystemStateMachine() {
        currentState = ElevatorSubsystemState.IDLE;
        System.out.println("Elevator Subsystem State Machine initialized in state: " + currentState);
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
    public void transitionToMoving() {
        currentState = ElevatorSubsystemState.MOVING;
        System.out.println("Transitioned to MOVING state.");
    }

    /**
     * Transitions the elevator subsystem to the UNLOADING_FOR_PASSENGERS state and prints a corresponding message.
     */
    public void transitionToUnloadPassengers() {
        currentState = ElevatorSubsystemState.UNLOADING_FOR_PASSENGERS;
        System.out.println("Transitioned to UNLOADING_FOR_PASSENGERS state.");
    }
}




