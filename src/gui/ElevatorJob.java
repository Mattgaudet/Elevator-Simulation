package gui;

public class ElevatorJob {
    private ElevatorJobType type;
    private int data;

    public ElevatorJob(ElevatorJobType type, int data) {
        this.type = type;
        this.data = data;
    }

    public ElevatorJobType getType() {
        return type;
    }

    public int getData() {
        return data;
    }
}
