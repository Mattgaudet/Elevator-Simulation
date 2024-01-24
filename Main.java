import elevator.ElevatorSubsystem;
import scheduler.Scheduler;
import floor.FloorSubsystem;

public class Main {


    public static void main(String[] args) {
        // Define the number of elevators and floors in your system
        int numElevators = 3;
        int totalFloors = 10;

        // Create a Scheduler instance
        Scheduler scheduler = new Scheduler(numElevators);

        // Create a FloorSubsystem instance
        // The FloorSubsystem reads from a file and sends requests to the Scheduler
        FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, totalFloors);

        // The threads for Scheduler and FloorSubsystem start within their constructors
        // The simulation begins here, with FloorSubsystem reading and sending requests
        // and Scheduler assigning these requests to Elevators

        // Since this is a simulation, you might want to let it run for a certain period 
        // or until a certain condition is met (e.g., all requests processed)

        // Example: Let the simulation run for a specified time
        try {
            Thread.sleep(20000); // Let the simulation run for 20 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted");
        }

        // Add additional logic if needed to gracefully stop threads and handle resources

        // Example: Print a message when the simulation ends
        System.out.println("Elevator simulation ended.");

        System.exit(0);
    }
}

