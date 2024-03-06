package elevator;

import common.Log;
import floor.ElevatorRequest;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * The transporting state executes the all requests in the elevatorQueue, as well as requests that are assigned
 * while moving in the state
 */
public class ElevatorTransportingState implements ElevatorState{
    /** Elevator context */
    private Elevator elevator;

    /**
     * Begins moving the elevator
     * @param elevator The elevator context
     */
    @Override
    public void action(Elevator elevator) {
        this.elevator = elevator;
        Log.print("Elevator " + elevator.getElevatorId() + " transitioned to TRANSPORTING state");
        simulateElevatorMovement();
    }

    /**
     * Simulate movement of the elevator for all requests in the elevator's request queue
     */
    public void simulateElevatorMovement() {
        Log.print("\n***********************************************\n");
        Log.print("Elevator " + elevator.getElevatorId() + " is currently on floor " + elevator.getCurrentFloor() + " with door " +
                elevator.getDoorStatus().name().toLowerCase() + " at " + LocalTime.now());
        int destinationFloor;
        while (!elevator.getElevatorQueue().isEmpty()) {
            // Extract the next destination floor from the queue
            ElevatorRequest request = elevator.getElevatorQueue().peek();
            destinationFloor = request.getButtonId();
            int startingFloor = request.getFloorNumber();

            // Determine the direction of movement
            ElevatorRequest.ButtonDirection currDirection = destinationFloor > startingFloor ? ElevatorRequest.ButtonDirection.UP : ElevatorRequest.ButtonDirection.DOWN;

            // Turn on the motor
            elevator.setMotorStatus(Elevator.MotorStatus.ON);

            //open doors if already on same floor as the request
            if (request.getFloorNumber() == elevator.getCurrentFloor()) {
                loadElevator("loading", elevator.getCurrentFloor());
                request.setLoaded();
            }
            //check if the elevator needs to move in opposite direction to get to starting floor
            if ((startingFloor < elevator.getCurrentFloor() && currDirection == ElevatorRequest.ButtonDirection.UP) || (startingFloor > elevator.getCurrentFloor() &&
                    currDirection == ElevatorRequest.ButtonDirection.DOWN)) {
                //direction to starting floor is the opposite of the direction the destination
                ElevatorRequest.ButtonDirection directionToStartingFloor = currDirection == ElevatorRequest.ButtonDirection.UP ?
                        ElevatorRequest.ButtonDirection.DOWN : ElevatorRequest.ButtonDirection.UP;
                //move the elevator to starting floor
                moveElevator(startingFloor, directionToStartingFloor, true);
            }
            //move to destination
            moveElevator(elevator.getElevatorQueue().peek().getButtonId(), currDirection, false);
        }
        // If the elevator has reached the destination floor and completed all requests, turn off the motor
        elevator.setMotorStatus(Elevator.MotorStatus.OFF);
        Log.print("Elevator " + elevator.getElevatorId() + " is waiting for next request at floor " + elevator.getCurrentFloor() +
                " with door " + elevator.getDoorStatus().name().toLowerCase() + " at " + LocalTime.now());
        Log.print("\n***********************************************\n");
        elevator.setState(Elevator.State.IDLE);
    }

    /**
     * Move the elevator to destination floor by moving to pick up the first request in the queue, and also completes
     * other requests when possible
     * @param destinationFloor the floor to go to
     * @param direction direction to move
     * @param isInitialPickup true if elevator must move in opposite direction for initial pickup
     */
    public void moveElevator(int destinationFloor, ElevatorRequest.ButtonDirection direction, boolean isInitialPickup) {
        int floorsToMove = Math.abs(elevator.getCurrentFloor() - destinationFloor);
        double tripTime = elevator.findTravelTime(floorsToMove);
        Log.print("Elevator " + elevator.getElevatorId() + " is moving " + direction.name().toLowerCase() +
                " from floor " + elevator.getCurrentFloor() + " to floor " + destinationFloor +
                ". Estimated travel time: " + tripTime + " ms");
        // Move the elevator from the current floor to the destination floor
        for (int floorsMoved = 0; floorsMoved < floorsToMove; floorsMoved++) {
            int nextFloor = direction == ElevatorRequest.ButtonDirection.UP ? elevator.getCurrentFloor() + 1 : elevator.getCurrentFloor() - 1;
            ArrayList<ElevatorRequest> removeList = new ArrayList<>();
            elevator.arrivedFloor(nextFloor);

            // Change the lamp status of the floor based on the direction
            if (direction == ElevatorRequest.ButtonDirection.UP) {
                // todo make it print only when direction changes
                //for debug only (can be removed)
                //Log.print("Requesting -> Lamp status of floors should change to " + direction.name());
                //elevatorSubsystem.changeLampStatus(ElevatorRequest.ButtonDirection.UP);

            } else {
                //elevatorSubsystem.changeLampStatus(ElevatorRequest.ButtonDirection.DOWN);
            }

            boolean doorsOpened = false;

            synchronized (elevator.getQueueLock()) {
                //check if new requests have been added to the front of the queue
                int newFloor = elevator.getElevatorQueue().peek().getFloorNumber();
                if(newFloor > destinationFloor && direction == ElevatorRequest.ButtonDirection.UP
                        || newFloor < destinationFloor && direction == ElevatorRequest.ButtonDirection.DOWN) {
                    floorsToMove += Math.abs(newFloor - destinationFloor);
                    destinationFloor = newFloor;
                }
                //check if any unload requests on this floor
                for (ElevatorRequest e : elevator.getElevatorQueue()) {
                    if (nextFloor == e.getButtonId() && e.isLoaded()) {
                        loadElevator("unloading", nextFloor); //unload the elevator
                        removeList.add(e);
                        doorsOpened = true;
                    }
                    //check if any load requests on this floor in same direction
                    else if (e.getFloorNumber() == nextFloor && (e.getButtonDirection() == direction && !isInitialPickup ||
                            isInitialPickup && e.getFloorNumber() == newFloor && e.getButtonDirection() != direction)) {
                        if (!doorsOpened) { //prevents doors from opening twice on same floor
                            loadElevator("loading", nextFloor); //load the elevator
                            doorsOpened = true;
                        } else {
                            Log.print("Additional passenger got on elevator at floor " + nextFloor + "!");
                        }
                        //adjust floorsToMove and destination to accommodate new request if necessary
                        if ((direction == ElevatorRequest.ButtonDirection.UP && e.getButtonId() > destinationFloor) ||
                                (direction == ElevatorRequest.ButtonDirection.DOWN && e.getButtonId() < destinationFloor)) {
                            floorsToMove += Math.abs(e.getButtonId() - destinationFloor);
                            destinationFloor = e.getButtonId();
                        }
                        e.setLoaded();
                    }
                }
                //remove completed requests
                for (ElevatorRequest e : removeList) {
                    e.setProcessed(); // this set the processed variable to true
                    elevator.getElevatorQueue().remove(e);
                }
            }
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
        Log.print("Elevator " + elevator.getElevatorId() + " is stopping for " + loadingType + " at floor " + nextFloor);
        elevator.setDoorStatus(Elevator.DoorStatus.OPEN);
        elevator.timeToLoadPassengers(1);
        //Log.print("Passenger " + loadingType + "!");
        elevator.setDoorStatus(Elevator.DoorStatus.CLOSED);
    }
}
