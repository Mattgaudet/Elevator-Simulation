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
     * @param args Unused for now.
     */
    public static void main(String[] args) {

        Thread schedulerThread, elevatorSubsystemThread, floorSubsystemThread;
        FloorSubsystem floorSubsystem = new FloorSubsystem();
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
