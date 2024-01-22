package scheduler;

import floor.FloorSubsystem;
import elevator.ElevatorSubsystem;

public class Scheduler {

    // # Add class properties here
    private FloorSubsystem floorSubsystem;
    private ElevatorSubsystem elevatorSubsystem;

    public Scheduler() {
        // # Initialize class properties here
        floorSubsystem = new FloorSubsystem();
        elevatorSubsystem = new ElevatorSubsystem();
    }

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
    }
}