package main;

import elevator.ElevatorSubsystem;
import floor.FloorSubsystem;
import scheduler.Scheduler;

/**
 * Contains the entrypoint of the application.
 */
public class Main {

    /**
     * The entrypoint of the application. Creates all of the systems.
     * @param args is the file path to the data. Default to res/input.txt
     *             if no arguments are provided.
     */
    public static void main(String[] args) {

        Thread schedulerThread, elevatorSubsystemThread, floorSubsystemThread;

        String filePath = "res/input.txt"; // Default file path
        if (args.length > 0) {
            filePath = args[0]; // Use provided file path
        }

        // Pass the file path to the FloorSubsystem constructor
        FloorSubsystem floorSubsystem = new FloorSubsystem(filePath);
        Scheduler scheduler = new Scheduler(floorSubsystem);
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(scheduler);

        schedulerThread = new Thread(scheduler, "Scheduler Thread");
        elevatorSubsystemThread = new Thread(elevatorSubsystem, "Elevator Subsystem Thread");
        floorSubsystemThread = new Thread(floorSubsystem, "Floor Subsystem Thread");

        schedulerThread.start();
        elevatorSubsystemThread.start();
        floorSubsystemThread.start();

        // TODO: FIXME
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);

    }
}
