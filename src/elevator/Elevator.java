package elevator;

import java.time.LocalTime;

import floor.ElevatorRequest;
import floor.ElevatorRequest.ButtonDirection;

public class Elevator {
    
    private int currentFloor = 0;  // default floor is 0
    private int elevatorId;
    private DoorStatus doorStatus = DoorStatus.CLOSED;  // default door status is closed
    private MotorStatus motorStatus = MotorStatus.OFF; 
    private ButtonDirection currDirection;  // current direction the elevator is moving in
    
    public enum DoorStatus {
        OPEN,
        CLOSED
    }
    
    public enum MotorStatus {
        ON,
        OFF
    }

    public Elevator(int elevatorId) {
        this.elevatorId = elevatorId;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public MotorStatus getMotorStatus() {
        return motorStatus;
    }

    public void setMotorStatus(MotorStatus motorStatus) {
        this.motorStatus = motorStatus;
        System.out.println("The motor is now " + motorStatus.name().toLowerCase() + "!");
    }

    public DoorStatus getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(DoorStatus doorStatus) {
        try {
            Thread.sleep(3000); // it takes 3 seconds to open/close door
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.doorStatus = doorStatus;
        System.out.println("Door is " + (doorStatus == DoorStatus.OPEN ? "Open!" : "Closed!"));
    }

    public void timeToLoadPassengers(int numPassengers) {
        try {
            Thread.sleep(numPassengers * 1000); // it takes 1 second to load/unload each passenger
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int findTravelTime(int startFloor, int endFloor) {
        int floorDistance = Math.abs(startFloor - endFloor);
        double travelTime = floorDistance * 0.5;         // Assuming the elevator travels at a speed of 0.5 seconds per floor
        return (int) (Math.round(travelTime * 1000));
    }

    // Updates the current floor of the elevator and prints the arrival message.
    public int arrivedFloor(int floorNum) {
        this.currentFloor = floorNum;
        System.out.println("Elevator " + this.elevatorId + " arrived at floor " + floorNum + " at " + LocalTime.now());
        return -1;
    }

    public void simulateElevatorMovement(ElevatorRequest elevatorRequest) {

        // Extract information from the request
        int destinationFloor = elevatorRequest.getFloorNumber();
        boolean isUpDirection = elevatorRequest.getButtonDirection() == ButtonDirection.UP;
        int initialFloor = this.currentFloor;
        double tripTime = findTravelTime(this.currentFloor, destinationFloor);
    
        // Set the current direction of the elevator
        this.currDirection = elevatorRequest.getButtonDirection();
    
        // Turn on the motor
        this.setMotorStatus(MotorStatus.ON);
    
        // Move the elevator from the current floor to the destination floor
        for (int floorsMoved = 0; floorsMoved <= Math.abs(initialFloor - destinationFloor); floorsMoved++) {
            int nextFloor = isUpDirection ? initialFloor + floorsMoved : initialFloor - floorsMoved;
            arrivedFloor(nextFloor);
    
            // If the elevator hasn't reached the destination floor yet, pause for the time it takes to travel one floor
            if (floorsMoved + 1 < Math.abs(initialFloor - destinationFloor)) {
                try {
                    Thread.sleep((int) tripTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Re-interrupt the thread
                    throw new RuntimeException("Thread was interrupted", e);
                }
            }
        }
    
        // If the elevator has reached the destination floor, turn off the motor
        this.setMotorStatus(MotorStatus.OFF);
    }
}
