package elevator;

import floor.ElevatorRequest;

public class ElevatorInfo {
    private int elevatorId;
    private Elevator.State currentState;
    private int currentFloor;
    private ElevatorRequest.ButtonDirection currDirection;

    public ElevatorInfo(int elevatorId, Elevator.State currentState, int currentFloor, ElevatorRequest.ButtonDirection currDirection) {
        this.elevatorId = elevatorId;
        this.currentState = currentState;
        this.currentFloor = currentFloor;
        this.currDirection = currDirection;
    }
    @Override
    public String toString() {
        return "ElevatorInfo{" +
                "elevatorId=" + elevatorId +
                ", currentState=" + currentState +
                ", currentFloor=" + currentFloor +
                ", currDirection=" + currDirection +
                '}';
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public Elevator.State getCurrentState() {
        return currentState;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public ElevatorRequest.ButtonDirection getCurrDirection() {
        return currDirection;
    }
}
