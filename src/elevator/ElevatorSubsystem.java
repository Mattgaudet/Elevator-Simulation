package elevator;

import java.util.*;

import floor.ElevatorRequest;

public class ElevatorSubsystem implements Runnable {
    private final Object lock;
    private int id;
    private Thread thread;
    private Set<Integer> elevatorButtons;
    private Set<Integer> elevatorLamps;
    private boolean motorState; // true for moving, false for stationary
    private boolean doorState; // true for open, false for closed
    private Queue<ElevatorRequest> requests = new LinkedList<>(); // Queue to hold the requests
    private int currentFloor;


    public ElevatorSubsystem(int id, Object lock, int totalFloors) {
        this.id = id;
        this.lock = lock;
        elevatorButtons = new HashSet<>();
        elevatorLamps = new HashSet<>();
        for (int i = 1; i <= totalFloors; i++) {
            elevatorButtons.add(i);
            elevatorLamps.add(i);
        }
        motorState = false;
        doorState = false;
        thread = new Thread(this);
        thread.start();
    }

    public int getId() {
        synchronized (lock) {
            return this.id;
        }
    }

    public void addRequest(ElevatorRequest request) {

        synchronized (lock) {
            requests.add(request);
            System.out.println("Elevator " + id + " received request: " + request);

            waitOneSecond();

            // Notify any waiting threads (like the elevator thread) that a new request is added
            lock.notify();
        }
    }

    public void pressButton(int floor) {
        synchronized (lock) {
            // Simulate button press
            elevatorButtons.add(floor);

            // Light the corresponding lamp
            setLamp(floor, true);
        }
    }

    public void setLamp(int floor, boolean state) {
        synchronized (lock) {
            // Simulate setting lamp state
            if (state) {
                elevatorLamps.add(floor);
            } else {
                elevatorLamps.remove(floor);
            }
        }
    }

    public void setMotorState(boolean state) {
        synchronized (lock) {
            // Simulate setting motor state
            motorState = state;
        }
    }

    public void setDoorState(boolean state) {
        synchronized (lock) {
            doorState = state;
            if (doorState) {
                System.out.println("Elevator " + id + ": Doors opened");
                waitOneSecond();
            } else {
                System.out.println("Elevator " + id + ": Doors closed");
                waitOneSecond();
            }

        }
    }

    public int getCurrentFloor() {
        synchronized (lock) {
            // Simulate getting the current floor

            // System.out.println("Elevator " + id + ": Current floor is " + currentFloor);

           return currentFloor;
        }
    }

    @Override
    public void run() {

        // The thread will run forever, waiting for requests and processing them

        while (true) {
            synchronized (lock) {
                while (requests.isEmpty()) {
                    try {
                        lock.wait(); // Wait for a request
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Elevator " + id + " interrupted");
                    }
                }

                ElevatorRequest request = requests.poll(); // Retrieve and remove the next request
                processRequest(request);
            }
        }
    }

     // Elevator operations

    private void processRequest(ElevatorRequest request) {
        moveToFloor(request.getFloorNumber());

        setDoorState(true); // Open doors
        System.out.println("Elevator " + id + ": Arrived at floor " + request.getFloorNumber());
        waitOneSecond(); // Simulate waiting time
        setDoorState(false); // Close doors
    }


    private void moveToFloor(int floor) {
        while (getCurrentFloor() != floor) {

            System.out.println("Elevator " + id + " moving to floor " + floor);

            // Logic to move the elevator (up or down) one floor at a time
            
            while (getCurrentFloor() < floor) {
                setMotorState(true); // Move up
                currentFloor++;
                waitOneSecond();
            }

            while (getCurrentFloor() > floor) {
                setMotorState(false); // Move down
                currentFloor--;
                waitOneSecond();
            }

            setMotorState(false); // Stop moving
        }
    }

    private void waitAtFloor() {
        
        System.out.println("Elevator " + id + " waiting at floor");

        waitOneSecond();
    }

    private void waitOneSecond() {
        try {
            Thread.sleep(1000); // Wait for 1 second (example)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Object getLock() {
        return lock;
    }


}