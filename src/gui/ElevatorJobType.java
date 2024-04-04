package gui;

/**
 * 
 */
public enum ElevatorJobType {
    MOVE,
    LOAD,
    UNLOAD,
    OPEN,
    CLOSE,
    TRANSIENT_FAULT,
    HARD_FAULT;

    public boolean isFault() {
        return this == TRANSIENT_FAULT || this == HARD_FAULT;
    }
}
