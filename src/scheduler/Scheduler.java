package scheduler;

import floor.ElevatorRequest;
import elevator.ElevatorSubsystem;
import java.util.*;

public class Scheduler implements Runnable {
    private List<ElevatorSubsystem> elevators;
    private Thread thread;
    private List<ElevatorRequest> elevatorRequests; // A list to hold the requests
    private int numFloors;

    public Scheduler(int numElevators) {
        elevators = new ArrayList<>();
        for (int i = 1; i <= numElevators; i++) {
            elevators.add(new ElevatorSubsystem(i, this, numFloors)); // Pass reference to the Scheduler
        }
        elevatorRequests = Collections.synchronizedList(new ArrayList<>());
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void addRequest(ElevatorRequest request) {
        elevatorRequests.add(request);
        System.out.println("Added request: " + request);
        notifyAll(); // Notify any waiting threads (like the scheduler thread) that a new request is added
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                while (elevatorRequests.isEmpty()) {
                    try {
                        wait(); // Wait until there is a request
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Set the interrupt flag
                        System.err.println("Scheduler interrupted");
                    }
                }

                ElevatorRequest request = elevatorRequests.remove(0); // Retrieve and remove the first request
                assignRequestToElevator(request);


            }
        }
    }

    private void assignRequestToElevator(ElevatorRequest request) {
        ElevatorSubsystem bestElevator = findBestElevator(request);
        if (bestElevator != null) {
            bestElevator.addRequest(request);
            synchronized (bestElevator.getLock()) {
                bestElevator.getLock().notify(); // Notify the elevator thread
            }
        } else {
            System.out.println("No suitable elevator found for request: " + request);
        }
    }
    

    private ElevatorSubsystem findBestElevator(ElevatorRequest request) {
        // Implement your logic here to find the best elevator
        return elevators.stream()
            .min(Comparator.comparingInt(elevator -> calculateDistance(elevator, request)))
            .orElse(null);
    }

    private int calculateDistance(ElevatorSubsystem elevator, ElevatorRequest request) {
        // Calculate the distance between the elevator and the request
        // This is a simple example and may not be accurate for your use case
        return Math.abs(elevator.getCurrentFloor() - request.getFloorNumber());
    }


}