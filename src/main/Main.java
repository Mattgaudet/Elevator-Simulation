package main;

import log.Log;

import elevator.Elevator;
import elevator.ElevatorSubsystem;
import scheduler.Scheduler;
import floor.FloorSubsystem;

public class Main {

    public static void main(String[] args) {

        // For disabling logs
        // Log.disable();

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

        // exit after 15 seconds
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);

    }

}
