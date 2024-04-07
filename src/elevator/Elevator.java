package elevator;

import common.Config;
import common.Log;
import floor.ElevatorRequest.ButtonDirection;
import floor.ElevatorRequest;
import elevator.ElevatorSubsystem;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;

/**
 * Represents a single 'Elevator' or 'Elevator Car'. Used by the ElevatorSubsystem
 * to schedule elevators for passengers.
 */
public class Elevator extends Thread{
    /** Current state of the elevator */
    private ElevatorState currentState; // SEND

    /** Holds all the states of the elevator */
    private Map<State, ElevatorState> states;

    /** For helping to synchronize the elevatorQueue */
    private Object queueLock = 0;

    /** The current floor the elevator is on. By default is 0. */
    private int currentFloor = 0; // SEND

    /** A unique elevator ID for sending specific messages to an elevator.  */
    private int elevatorId; // SEND

    /** The status of the door. Either open or closed. By default is closed.  */
    private DoorStatus doorStatus = DoorStatus.CLOSED;

    /** The status of the directional motor. Either up, down, or off. By default is off. */
    private MotorStatus motorStatus = MotorStatus.OFF;

    /** The direction of movement of the elevator. Either up, down, or none. */
    private ButtonDirection currDirection = ButtonDirection.NONE; // SEND
    /** The queue of requests assigned to the elevator */
    private PriorityQueue<ElevatorRequest> elevatorQueue;

    /** The elevator subsystem to use. */
    private ElevatorSubsystem elevatorSubsystem;
    /** Timer for elevator transporting state fault detection */
    private long transportingTimeout; // 1 minute

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
     * States for the elevator
     */
    public enum State {
        IDLE,
        TRANSPORTING,
        FAULT,
        UNKNOWN
    }

    /**
     * Create a new elevator with a unique ID.
     * @param elevatorId
     */
    public Elevator(int elevatorId, ElevatorSubsystem elevatorSubsystem) {
        this.elevatorId = elevatorId;
        this.elevatorQueue = new PriorityQueue<>();
        this.elevatorSubsystem = elevatorSubsystem;
        this.states = new HashMap<>();
        addState(State.IDLE, new ElevatorIdleState());
        addState(State.TRANSPORTING, new ElevatorTransportingState(elevatorSubsystem));
        addState(State.FAULT, new ElevatorFaultState());
    }

    public ElevatorState getCurrentState(){
        return currentState;
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
        //Log.print("Elevator " + elevatorId +" door is " + doorStatus.name().toLowerCase() + "!"); for debug
    }

    public ButtonDirection getCurrDirection() {
        return currDirection;
    }
    public void setDirection(ButtonDirection b) {this.currDirection = b;}

    public void sendRequestBackToScheduler(ElevatorRequest request) throws IOException {
        int schedulerPort = 5000; // Example port number for Scheduler
        String schedulerHost = "localhost"; // Scheduler host, change as needed
        DatagramSocket socket = new DatagramSocket();
        InetAddress schedulerAddress = InetAddress.getByName(schedulerHost);
        byte[] sendData = request.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, schedulerAddress, schedulerPort);
        socket.send(sendPacket);

        // Print the data sent, for testing
        String sentDataString = new String(sendData, StandardCharsets.UTF_8);
        Log.print("Elevator " + elevatorId + " full, sent request BACK to Scheduler: " + sentDataString);
    }

    /**
     * Function to add a request to this Elevator's request queue. Synchronizing on queueLock rather than elevatorQueue
     * so that elevatorQueue can be reinitialized without causing issues
     * @param request a request received from the scheduler
     */
    public void addRequestToElevatorQueue(ElevatorRequest request) {
        synchronized (queueLock) {
            // We are assuming each request is considered one person
            if (elevatorQueue.size() + 1 > Config.MAX_PASSENGERS) {
                Log.print("Elevator " + elevatorId + " is full. Cannot add more passengers.");
                try {
                    sendRequestBackToScheduler(request); // Send the request back to scheduler for re-assignment
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

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
            long estimatedTime = (long) Config.TIME_TO_TRAVEL_1_FLOOR * Math.abs(request.getFloorNumber() - request.getButtonId());
            estimatedTime += (long) Config.TIME_TO_TRAVEL_1_FLOOR * Math.abs(currentFloor - request.getFloorNumber());
            estimatedTime += 2 * Config.LOAD_TIME + 4 * Config.DOOR_TIME + 30000; //add 30 seconds buffer time
            if (elevatorQueue.isEmpty()) {
                transportingTimeout = estimatedTime;
            } else {
                transportingTimeout += estimatedTime;
            }
            request.setStartTime(LocalTime.now());
            elevatorQueue.add(request);
            queueLock.notifyAll();
        }
    }

    /**
     * Getter for synchronization on queueLock
     * @return queueLock object
     */
    public Object getQueueLock() {
        return queueLock;
    }

    public void removeRequestFromElevatorQueue() {
        ElevatorRequest nextRequest = elevatorQueue.poll(); // Remove the next request if available, else will be null
        // TODO: Don't need this assignment eventually, but useful for debugging
    }

    /**
     * Getter for the elevatorQueue
     * @return elevatorQueue the priorityQueue for the elevator
     */
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
     * Allows for test thread to edit the start time of transporting state to simulate timeout fault
     */
    public void changeTime() {
        ((ElevatorTransportingState) this.currentState).editStartTime(300000);
    }

    /**
     * Change the current floor of the elevator.
     * @param floorNum The current floor of the elevator.
     * @return Unused.
     */
    public int arrivedFloor(int floorNum) {
        this.currentFloor = floorNum;
        elevatorSubsystem.addFloorMoved();
        //Log.print("Elevator " + this.elevatorId + " reached floor " + floorNum + " at " + LocalTime.now()); for debug
        return -1;
    }

    /**
     * Add a ElevatorState to map states
     * @param name String name of state to be added
     * @param state ElevatorState to be added
     */
    public void addState(State name, ElevatorState state) { states.put(name, state); }

    /**
     * Set the state
     * @param s String state name
     */
    public void setState(State s) {
        this.currentState = getState(s);
        this.currentState.action(this);

    }

    /**
     * Return this elevator's transportingTimeout
     * @return transportingTimeout
     */
    public long getTransportingTimeout() {
        return transportingTimeout;
    }

    /**
     * Get state from hashmap
     * @param s string state name
     * @return Corresponding ElevatorState
     */
    public ElevatorState getState(State s) { return states.get(s); }

    /**
     * Sets the initial state to IdleState
     */
    @Override
    public void run() {
        setState(State.IDLE);
    }
}
