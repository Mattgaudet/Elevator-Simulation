package elevator;

import common.Config;
import common.Log;
import floor.ElevatorRequest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * The transporting state executes the all requests in the elevatorQueue, as well as requests that are assigned
 * while moving in the state
 */
public class ElevatorTransportingState implements ElevatorState{
    /** Elevator context */
    private Elevator elevator;
    private ElevatorSubsystem elevatorSubsystem;
    private long startTime;

    /**
     * Constructor for the transporting state
     * @param elevatorSubsystem the elevator subsystem
     */
    public ElevatorTransportingState(ElevatorSubsystem elevatorSubsystem) {
        this.elevatorSubsystem = elevatorSubsystem;
    }

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
                loadElevator("loading", elevator.getCurrentFloor(), request);
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
        if(elevator.getCurrentState() instanceof ElevatorTransportingState) { //only do if still in transporting state
            elevator.setMotorStatus(Elevator.MotorStatus.OFF);
            Log.print("Elevator " + elevator.getElevatorId() + " is waiting for next request at floor " + elevator.getCurrentFloor() +
                    " with door " + elevator.getDoorStatus().name().toLowerCase() + " at " + LocalTime.now());
            Log.print("\n***********************************************\n");
            elevator.setDirection(ElevatorRequest.ButtonDirection.NONE);
            elevator.setState(Elevator.State.IDLE);
        }
    }
    /**
     * Handle timeout error when elevator remains in transporting state for too long
     */
    private void handleTimeoutError() {
        Log.print("Elevator " + elevator.getElevatorId() + " timed out in transporting state.");
        elevator.setState(Elevator.State.FAULT);
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
        elevator.setDirection(direction);
        Log.print("Elevator " + elevator.getElevatorId() + " is moving " + direction.name().toLowerCase() +
                " from floor " + elevator.getCurrentFloor() + " to floor " + destinationFloor +
                ". Estimated travel time: " + tripTime + " ms");



        startTime = System.currentTimeMillis();

        int unloadedCount;
        int loadedCount;
        
        // Move the elevator from the current floor to the destination floor
        for (int floorsMoved = 0; floorsMoved < floorsToMove; floorsMoved++) {
            // Calculate elapsed time at each iteration
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;
            Log.print("Elevator " + elevator.getElevatorId() +": Elapsed time since start: " + elapsedTime + " ms");

            // Check if elapsed time exceeds the timeout threshold
            if (elapsedTime > elevator.getTransportingTimeout()) {
                Log.print("Transporting state exceeded timeout threshold.");
                handleTimeoutError(); // Handle timeout error
                return; // Exit method to stop further processing
            }
            int nextFloor = direction == ElevatorRequest.ButtonDirection.UP ? elevator.getCurrentFloor() + 1 : elevator.getCurrentFloor() - 1;
            ArrayList<ElevatorRequest> removeList = new ArrayList<>();
            elevator.arrivedFloor(nextFloor);

            boolean doorsOpened = false;

            synchronized (elevator.getQueueLock()) {

                if (elevator.getCurrentState() instanceof ElevatorFaultState) {
                    return;
                } // prevent null pointer exception if elevator is assigned a faulty state

                //check if new requests have been added to the front of the queue
                int newFloor = elevator.getElevatorQueue().peek().getFloorNumber();

                if (newFloor > destinationFloor && direction == ElevatorRequest.ButtonDirection.UP
                        || newFloor < destinationFloor && direction == ElevatorRequest.ButtonDirection.DOWN) {
                    floorsToMove += Math.abs(newFloor - destinationFloor);
                    destinationFloor = newFloor;
                }

                // Check if any unload requests on this floor
               unloadedCount = 0;
               loadedCount = 0;

                for (ElevatorRequest e : elevator.getElevatorQueue()) {
                    if (nextFloor == e.getButtonId() && e.isLoaded()) {
                        loadElevator("unloading", nextFloor, e); // Unload the elevator
                        removeList.add(e);
                        doorsOpened = true;
                        unloadedCount++;
                    }
                    
                    // Check if any load requests on this floor in the same direction
                    else if (e.getFloorNumber() == nextFloor && (e.getButtonDirection() == direction && !isInitialPickup ||
                            isInitialPickup && e.getFloorNumber() == newFloor && e.getButtonDirection() != direction)) {
                        if (!doorsOpened) { // Prevents doors from opening twice on the same floor
                            loadElevator("loading", nextFloor, e); // Load the elevator
                            doorsOpened = true;
                            loadedCount++;
                        } else {
                            Log.print("Additional passenger got on elevator at floor " + nextFloor + "!");
                           loadedCount++;
                        }
                        // Adjust floorsToMove and destination to accommodate new request if necessary
                        if ((direction == ElevatorRequest.ButtonDirection.UP && e.getButtonId() > destinationFloor) ||
                                (direction == ElevatorRequest.ButtonDirection.DOWN && e.getButtonId() < destinationFloor)) {
                            floorsToMove += Math.abs(e.getButtonId() - destinationFloor);
                            destinationFloor = e.getButtonId();
                        }
                        e.setLoaded();
                    }
                }

                // Send the elevator's current state packet to the FloorSubsystem (port 12345)
                sendElevatorState(elevator.getElevatorId(), elevator.getCurrentState().toString(), nextFloor, direction, destinationFloor, unloadedCount, loadedCount);

                //remove completed requests
                for (ElevatorRequest e : removeList) {
                    e.setProcessed(); // this set the processed variable to true
                    elevatorSubsystem.sendCompletedElevatorRequest(e); //notify scheduler request completed
                    elevator.getElevatorQueue().remove(e); //remove from elevator queue
                }
            }

            // If the elevator hasn't reached the destination floor yet, pause for the time
            // it takes to travel one floor
            if (floorsMoved + 1 < floorsToMove) {
                try {
                    Thread.sleep(Config.TIME_TO_TRAVEL_1_FLOOR);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Re-interrupt the thread
                    throw new RuntimeException("Thread was interrupted", e);
                }
            }
        }



    }



    // Send the elevator's current state with the next floor, direction, destination floor, unloaded count, and loaded count to the FloorSubsystem 
    
    private void sendElevatorState(int elevatorId, String state, int nextFloor, ElevatorRequest.ButtonDirection direction, int destinationFloor, int unloadedCount, int loadedCount) {
        String infoString = String.format("%d;%s;%d;%s;%d;%d;%d",
                elevatorId,
                state,
                nextFloor,
                direction,
                destinationFloor,
                unloadedCount,
                loadedCount);
    
        byte[] infoBytes = infoString.getBytes(StandardCharsets.UTF_8);
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        int port = 12345; // The FloorSubsystem listen port
        DatagramPacket packet = new DatagramPacket(infoBytes, infoBytes.length, address, port);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }









    /**
     * Opens the doors, waits for passengers to load/unload, then closes doors
     * @param loadingType use "loading" or "unloading"
     * @param nextFloor the floor that the elevator is stopped at
     * @throws InterruptedException 
     */
    public void loadElevator(String loadingType, int nextFloor, ElevatorRequest er) {
        Log.print("Elevator " + elevator.getElevatorId() + " is stopping for " + loadingType + " at floor " + nextFloor);
        while(elevator.getElevatorQueue().peek().getFault().equals("DOOR_NOT_OPEN")) {
            Log.print(">> Elevator " + elevator.getElevatorId() + " door opening failed due to fault, retrying doors");

            // Send the FAULT to the FloorSubsystem
            String InfoString = elevator.getElevatorQueue().peek().getFault() + " fault encountered by Elevator " + elevator.getElevatorId() + " at floor " + nextFloor;
            byte[] directionBytes = InfoString.getBytes(StandardCharsets.UTF_8);
            InetAddress address = null;
            try {
                address = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } // the FloorSubsystem listen address
            int port = 12345; // the FloorSubsystem listen port
            DatagramPacket packet = new DatagramPacket(directionBytes, directionBytes.length, address, port);
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Sleep for 20 seconds (transient fault time)
            try{
                Thread.sleep(Config.TRANSIENT_FAULT_TIME);
            } catch (InterruptedException e) {}
            er.removeFault();
        }
        elevator.setDoorStatus(Elevator.DoorStatus.OPEN);
        elevator.timeToLoadPassengers(1);
        //if there is a DOOR_NOT_CLOSE fault, handle as transient fault: reopen door and wait, then try to close again
        while(elevator.getElevatorQueue().peek().getFault().equals("DOOR_NOT_CLOSE")) {
            Log.print(">> Elevator " + elevator.getElevatorId() + " door closing failed due to fault");
           
            // Send the FAULT to the FloorSubsystem
            String InfoString = elevator.getElevatorQueue().peek().getFault() + " fault encountered by Elevator " + elevator.getElevatorId() + " at floor " + nextFloor;
            byte[] directionBytes = InfoString.getBytes(StandardCharsets.UTF_8);
            InetAddress address = null;
            try {
                address = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } // the FloorSubsystem listen address
            int port = 12345; // the FloorSubsystem listen port
            DatagramPacket packet = new DatagramPacket(directionBytes, directionBytes.length, address, port);
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(Config.TRANSIENT_FAULT_TIME);
            } catch (InterruptedException e) {}
            er.removeFault();
        }

        elevator.setDoorStatus(Elevator.DoorStatus.CLOSED);
    }

    /**
     * Allow unit tests to modify start time to simulate timeout fault
     * @param time
     */
    public void editStartTime(long time) {
        startTime = startTime - time;
    }
        @Override
    public String toString() {
        return "TRANSPORTING";
    }
}
