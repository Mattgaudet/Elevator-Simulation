package gui;

/**
 * 
 */
public class ElevatorJob {
    /** */
    private ElevatorJobType type;
    /** */
    private int data;

    /**
     * 
     * @param type
     * @param data
     */
    public ElevatorJob(ElevatorJobType type, int data) {
        this.type = type;
        this.data = data;
    }

    /**
     * 
     * @return
     */
    public ElevatorJobType getType() {
        return type;
    }

    /**
     * 
     * @return
     */
    public int getData() {
        return data;
    }

    /**
     * 
     * @return
     */
    public boolean isFault() {
        return type.isFault();
    }
}
