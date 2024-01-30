package main;

import elevator.Elevator;
import elevator.ElevatorSubsystem;
import scheduler.Scheduler;
import floor.FloorSubsystem;

public class Main {

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

        // exit after 10 seconds
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);

    }

}
