package elevator;

import common.Config;
import common.Log;
import floor.ElevatorRequest.ButtonDirection;
import floor.ElevatorRequest;
import elevator.ElevatorSubsystem;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.PriorityQueue;

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
    /** The queue of requests assigned to the elevator */
    private PriorityQueue<ElevatorRequest> elevatorQueue;

    /** The elevator subsystem to use. */
    private ElevatorSubsystem elevatorSubsystem;

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
    public Elevator(int elevatorId, ElevatorSubsystem elevatorSubsystem) {
        this.elevatorId = elevatorId;
        this.elevatorQueue = new PriorityQueue<>();
        this.elevatorSubsystem = elevatorSubsystem;
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
                elevatorQueue = new PriorityQueue<>(); // default is up
            } else {
                elevatorQueue = new PriorityQueue<>(Comparator.reverseOrder());
            }
        } else {
            if (request.getButtonDirection() == ButtonDirection.UP) {
                assert request.getButtonId() >= elevatorQueue.peek().getButtonId(); // this should not happen
            } else {
                assert request.getButtonId() <= elevatorQueue.peek().getButtonId(); // this should not happen
            }
        }
        elevatorQueue.add(request);
    }

    public void removeRequestFromElevatorQueue() {
        ElevatorRequest nextRequest = elevatorQueue.poll(); // Remove the next request if available, else will be null
        // TODO: Don't need this assignment eventually, but useful for debugging
    }

    public PriorityQueue<ElevatorRequest> getElevatorQueue(){
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
     * @param numFloors The number of floors.
     * @return The travel time in milliseconds.
     */
    public int findTravelTime(int numFloors) {
        double travelTime = numFloors * 1.0 / Config.FLOORS_PER_SECOND; // Assuming the elevator travels at a speed of 0.5 seconds per floor
        return (int) (Math.round(travelTime * 1000));
    }

    /**
     * Change the current floor of the elevator.
     * @param floorNum The current floor of the elevator.
     * @return Unused.
     */
    public int arrivedFloor(int floorNum) {
        this.currentFloor = floorNum;
        Log.print("Elevator " + this.elevatorId + " reached floor " + floorNum + " at " + LocalTime.now());
        return -1;
    }

    /**
     * Simulate the movement of the elevator.
     */
    public void simulateElevatorMovement() {
        Log.print("\n***********************************************\n");
        Log.print("Elevator " + this.elevatorId + " is currently on floor " + this.currentFloor + " with door " +
                this.doorStatus.name().toLowerCase() + " at " + LocalTime.now());
        int destinationFloor;
        while (!elevatorQueue.isEmpty()) {
            // Extract the next destination floor from the queue
            ElevatorRequest request = elevatorQueue.peek();
            destinationFloor = request.getButtonId();
            int startingFloor = request.getFloorNumber();

            // Determine the direction of movement
            currDirection = destinationFloor > startingFloor ? ButtonDirection.UP : ButtonDirection.DOWN;

            // Turn on the motor
            this.setMotorStatus(MotorStatus.ON);

            //open doors if already on same floor as the request
            if (request.getFloorNumber() == currentFloor) {
                loadElevator("loading", currentFloor);
                request.setLoaded();
            }
            //check if the elevator needs to move in opposite direction to get to starting floor
            if ((startingFloor < currentFloor && currDirection == ButtonDirection.UP) || (startingFloor > currentFloor &&
                    currDirection == ButtonDirection.DOWN)) {
                //direction to starting floor is the opposite of the direction the destination
                ButtonDirection directionToStartingFloor = currDirection == ButtonDirection.UP ?
                        ButtonDirection.DOWN : ButtonDirection.UP;
                //move the elevator to starting floor
                moveElevator(startingFloor, directionToStartingFloor, true);
            }
            //move to destination
            moveElevator(request.getButtonId(), currDirection, false);
        }
        // If the elevator has reached the destination floor and completed all requests, turn off the motor
        this.setMotorStatus(MotorStatus.OFF);
        Log.print("Elevator " + this.elevatorId + " is waiting for next request at floor " + currentFloor +
                " with door " + this.doorStatus.name().toLowerCase() + " at " + LocalTime.now());
        Log.print("\n***********************************************\n");
    }

    /**
     * Move the elevator to destination floor by moving to pick up the first request in the queue, and also completes
     * other requests when possible
     * @param destinationFloor the floor to go to
     * @param direction
     * @param isInitialPickup true if elevator must move in opposite direction for initial pickup
     */
    public void moveElevator(int destinationFloor, ButtonDirection direction, boolean isInitialPickup) {
        int floorsToMove = Math.abs(currentFloor - destinationFloor);
        double tripTime = findTravelTime(floorsToMove);
        Log.print("Elevator " + this.elevatorId + " is moving " + direction.name().toLowerCase() +
                " from floor " + this.currentFloor + " to floor " + destinationFloor +
                ". Estimated travel time: " + tripTime + " ms");
        // Move the elevator from the current floor to the destination floor
        for (int floorsMoved = 0; floorsMoved < floorsToMove; floorsMoved++) {
            int nextFloor = direction == ButtonDirection.UP ? currentFloor + 1 : currentFloor - 1;
            ArrayList<ElevatorRequest> removeList = new ArrayList<>();
            arrivedFloor(nextFloor);

                // Change the lamp status of the floor based on the direction
                if (direction == ButtonDirection.UP) {
                    // for debug only (can be removed)
                    // Log.print("Requesting -> Lamp status of floors should change to " + direction.name());
                    elevatorSubsystem.changeLampStatus(ButtonDirection.UP);

                } else {
                    elevatorSubsystem.changeLampStatus(ButtonDirection.DOWN);
                }

            boolean doorsOpened = false;
            //check if any unload requests on this floor
            for(ElevatorRequest e : elevatorQueue) {
                if(nextFloor == e.getButtonId() && e.isLoaded()) {
                    loadElevator("unloading", nextFloor); //unload the elevator
                    removeList.add(e);
                    doorsOpened = true;
                }
                //check if any load requests on this floor in same direction
                else if(e.getFloorNumber() == nextFloor && ((e.getButtonDirection() != direction && isInitialPickup) ||
                        (e.getButtonDirection() == direction && !isInitialPickup))){
                    if(!doorsOpened) { //prevents doors from opening twice on same floor
                        loadElevator("loading", nextFloor); //load the elevator
                        doorsOpened = true;
                    }
                    else { Log.print("Additional passenger got on elevator at floor " + nextFloor + "!");}
                    //adjust floorsToMove and destination to accommodate new request if necessary
                    if((direction == ButtonDirection.UP && e.getButtonId() > destinationFloor) ||
                            (direction == ButtonDirection.DOWN && e.getButtonId() < destinationFloor)) {
                        floorsToMove += Math.abs(e.getButtonId() - destinationFloor);
                        destinationFloor = e.getButtonId();
                    }
                    e.setLoaded();
                }
            }
            //remove completed requests
            for(ElevatorRequest e : removeList) { elevatorQueue.remove(e); }
            // If the elevator hasn't reached the destination floor yet, pause for the time
            // it takes to travel one floor
            if (floorsMoved + 1 < floorsToMove) {
                try {
                    Thread.sleep((int) tripTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Re-interrupt the thread
                    throw new RuntimeException("Thread was interrupted", e);
                }
            }
        }
    }

    /**
     * Opens the doors, waits for passengers to load/unload, then closes doors
     * @param loadingType use "loading" or "unloading"
     * @param nextFloor the floor that the elevator is stopped at
     */
    public void loadElevator(String loadingType, int nextFloor) {
        Log.print("Stopping for " + loadingType + " at floor " + nextFloor);
        setDoorStatus(DoorStatus.OPEN);
        timeToLoadPassengers(1);
        //Log.print("Passenger " + loadingType + "!");
        setDoorStatus(DoorStatus.CLOSED);
    }
}
