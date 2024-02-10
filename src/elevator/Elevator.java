package elevator;

import config.Config;
import floor.ElevatorRequest.ButtonDirection;
import floor.ElevatorRequest;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

import log.Log;

/**
 * Represents a single 'Elevator' or 'Elevator Car'. Used by the ElevatorSubsystem
 * to schedule elevators for passengers.
 */
public class Elevator {

    /** The current floor the elevator is on. By default is 0. */
    private int currentFloor = 0;

    /** A unique elevator ID for sending specific messages to an elevator.  */
    private int elevatorId;

    /** The status of the door. Either open or closed. By default is closed.  */
    private DoorStatus doorStatus = DoorStatus.CLOSED;

    /** The status of the directional motor. Either up, down, or off. By default is off. */
    private MotorStatus motorStatus = MotorStatus.OFF;

    /** The direction of movement of the elevator. Either up, down, or none. */
    private ButtonDirection currDirection = ButtonDirection.NONE;

    private PriorityQueue<Integer> elevatorQueue;

    /**
     * The door status.
     */
    public enum DoorStatus {
        /** The door is open. */
        OPEN,
        /** The door is closed. */
        CLOSED
    }

    /**
     * The status of the directional motor.
     */
    public enum MotorStatus {
        /** The motor is on. */
        ON,
        /** The motor is off. */
        OFF
    }

    /**
     * Create a new elevator with a unique ID.
     * @param elevatorId
     */
    public Elevator(int elevatorId) {
        this.elevatorId = elevatorId;
        this.elevatorQueue = new PriorityQueue<>(Comparator.comparingInt(i -> i));
    }

    /**
     * Get the current floor the elevator is on.
     * @return The current floor the elevator is on.
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Get the elevator ID.
     * @return The elevator ID.
     */
    public int getElevatorId() {
        return elevatorId;
    }

    /**
     * Get the status of the directional motor.
     * @return The status of the directional motor.
     */
    public MotorStatus getMotorStatus() {
        return motorStatus;
    }

    /**
     * Set the status of the directional motor.
     * @param motorStatus The status of the directional motor.
     */
    public void setMotorStatus(MotorStatus motorStatus) {
        this.motorStatus = motorStatus;
        Log.print("The motor is now " + motorStatus.name().toLowerCase() + "!");
    }

    /**
     * Get the status of the door.
     * @return The status of the door.
     */
    public DoorStatus getDoorStatus() {
        return doorStatus;
    }

    /**
     * Set the status of the door. Takes Config.DOOR_TIME seconds to open/close.
     * @param doorStatus The status of the door.
     */
    public void setDoorStatus(DoorStatus doorStatus) {
        try {
            Thread.sleep(Config.DOOR_TIME); // it takes 3 seconds to open/close door
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.doorStatus = doorStatus;
        Log.print("Door is " + doorStatus.name().toLowerCase() + "!");
    }

    /**
     * Function to add a request to this Elevator's request queue
     * @param request a request received from the scheduler
     */
    public void addRequestToElevatorQueue(ElevatorRequest request) {
        if (elevatorQueue.isEmpty()) {
            if (request.getButtonDirection() == ButtonDirection.UP) {
                PriorityQueue<Integer> queue = new PriorityQueue<>(); // default is up
            } else {
                PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.reverseOrder());
            }
        } else {
            if (request.getButtonDirection() == ButtonDirection.UP) {
                assert request.getButtonId() >= elevatorQueue.peek(); // this should not happen
            } else {
                assert request.getButtonId() <= elevatorQueue.peek(); // this should not happen

            }
        }
        elevatorQueue.add(request.getButtonId());
    }

    public void removeRequestFromElevatorQueue() {
        Integer nextRequest = elevatorQueue.poll(); // Remove the next request if available, else will be null
        // TODO: Don't need this assignment eventually, but useful for debugging
    }

    public PriorityQueue<Integer> getElevatorQueue(){
        return this.elevatorQueue;
    }

    /**
     * Wait to load passengers. Each passenger takes Config.LOAD_TIME seconds to load.
     * @param numPassengers The number of passengers to load.
     */
    public void timeToLoadPassengers(int numPassengers) {
        try {
            Thread.sleep(numPassengers * Config.LOAD_TIME); // it takes 1 second to load/unload each passenger
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate the travel time from one floor to another.
     * @param startFloor The starting the floor.
     * @param endFloor The destination floor.
     * @return The travel time in milliseconds.
     */
    public int findTravelTime(int startFloor, int endFloor) {
        int floorDistance = Math.abs(startFloor - endFloor);
        double travelTime = floorDistance * 1.0 / Config.FLOORS_PER_SECOND; // Assuming the elevator travels at a speed of 0.5 seconds per floor
        return (int) (Math.round(travelTime * 1000));
    }

    /**
     * Change the current floor of the elevator.
     * @param floorNum The current floor of the elevator.
     * @return Unused.
     */
    public int arrivedFloor(int floorNum) {
        this.currentFloor = floorNum;
        Log.print("Elevator " + this.elevatorId + " arrived at floor " + floorNum + " at " + LocalTime.now());
        return -1;
    }

    /**
     * Simulate an elevator movement. Moves according to the elevator request.
     * @param elevatorRequest The elevator request.
     */
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

            // If the elevator hasn't reached the destination floor yet, pause for the time
            // it takes to travel one floor
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
