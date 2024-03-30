package elevator;

import common.Log;
import floor.ElevatorRequest;

public class ElevatorFaultState implements ElevatorState {
    @Override
    public void action(Elevator elevator) {
        Log.print("Elevator " + elevator.getElevatorId() + " transitioned to FAULT state at " + java.time.LocalDateTime.now());
        // Perform actions when the elevator enters the fault state
        elevator.getElevatorQueue().clear();
        stopElevatorAtNearestFloor(elevator);
        openDoors(elevator);
        displayErrorMessage(elevator);
        
    }

    private void stopElevatorAtNearestFloor(Elevator elevator) {
        // Logic to stop the elevator at the nearest floor
        int currentFloor = elevator.getCurrentFloor();
        int destinationFloor = currentFloor; // Assume the current floor is the nearest

        // Check if the elevator is between floors
        if (elevator.getMotorStatus() == Elevator.MotorStatus.ON) {
            // Determine the direction of movement
            ElevatorRequest.ButtonDirection currDirection = elevator.getCurrDirection();
            destinationFloor = currDirection == ElevatorRequest.ButtonDirection.UP ? currentFloor + 1 : currentFloor - 1;
        }

        // Move the elevator to the nearest floor
        Log.print("Moving Elevator " + elevator.getElevatorId() + " to the nearest floor (i.e. floor " + destinationFloor + ")");
        elevator.arrivedFloor(destinationFloor);
        Log.print("Elevator " + elevator.getElevatorId() + " stopped at floor " + destinationFloor);
        Log.print("Turning Elevator " + elevator.getElevatorId() + " motor Off for safety and Opening its doors");
        elevator.setMotorStatus(Elevator.MotorStatus.OFF);
    }

    private void openDoors(Elevator elevator) {
        // Logic to open the elevator doors if safe
        if (elevator.getDoorStatus() == Elevator.DoorStatus.CLOSED) {
            elevator.setDoorStatus(Elevator.DoorStatus.OPEN);
        }
    }

    private void displayErrorMessage(Elevator elevator) {
        // Logic to display an error message or alarm
        Log.print("Elevator " + elevator.getElevatorId() + " is currently in FAULT state. Please contact maintenance.");
        // Additional code to display the message on the elevator's display panel or sound an alarm ? Maybe 
    }

    @Override
    public String toString() {
        return "FAULT";
    }
}