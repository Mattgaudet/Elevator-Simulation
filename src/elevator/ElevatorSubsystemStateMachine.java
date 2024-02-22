package elevator;

/**
 * Represents the state of the elevator subsystem.
 */
public class ElevatorSubsystemStateMachine {

    /** The current state of the elevator subsystem.  */
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
     * Initializes the elevator subsystem state machine in the IDLE state and prints an initialization message.
     */
    public ElevatorSubsystemStateMachine() {
        startIdling();
    }

    /**
     * Resets the elevator subsystem to the IDLE state and prints a corresponding message.
     */
    public void startIdling() {
        currentState = ElevatorSubsystemState.IDLE;
        System.out.println("Elevator Subsystem reset to IDLE state and stopped! ");
    }

    /**
     * Starts the processing if the current state is IDLE; otherwise, throws an IllegalStateException.
     */
    public void startProcessing() {
        currentState = ElevatorSubsystemState.PROCESSING;
        System.out.println("Elevator Subsystem transitioned to Processing requests state.");
    }

    /**
     * Transitions the elevator subsystem to the MOVING state and prints a corresponding message.
     */
    public void startTransporting() {
        currentState = ElevatorSubsystemState.TRANSPORTING;
        System.out.println("Transitioned to TRANSPORTING state.");
    }

    /**
     * Gets the current state of the elevator subsystem.
     * @return The current state of the elevator subsystem.
     */
    public ElevatorSubsystemState getCurrentState() {
        return currentState;
    }
}
