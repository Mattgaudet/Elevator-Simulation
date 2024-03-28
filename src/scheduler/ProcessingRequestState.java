package scheduler;

import common.Config;
import common.Log;
import elevator.Elevator;
import floor.ElevatorRequest;
import floor.ElevatorRequest.ButtonDirection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This state requests elevator locations from the scheduler and then selects the best elevator to use
 */
public class ProcessingRequestState implements SchedulerState {
    @Override
    public void processRequest(Scheduler scheduler, byte[] requestData) {
        ElevatorRequest request = parseRequestFromFloorSubsystem(requestData);
        if (request != null) {
            System.out.println("Received and parsed request: " + request.toString());
        } else {
            System.out.println("Failed to parse the request from received data.");
        }
        scheduler.addToRequestQueue(request);
        String elevatorsInfo = scheduler.getElevatorsInfo();
        int elevatorID = selectElevator(scheduler, request, elevatorsInfo);   // currently, always 0
        scheduler.setState(new ElevatorDispatchState());
        Log.print("Scheduler: State transitioned to ELEVATOR DISPATCH STATE.");
        scheduler.state.processRequest(scheduler, request, elevatorID);
    }

    /**
     * Create an elevatorRequest object from a byte array
     * @param requestData the request data
     * @return the new ElevatorRequest created
     */
    private ElevatorRequest parseRequestFromFloorSubsystem(byte[] requestData) {
        return new ElevatorRequest(requestData);
    }

    @Override
    public void processRequest(Scheduler scheduler, ElevatorRequest request, int elevatorID) {
        // Not needed
    }

    /**
     * Selects the appropriate elevator based on the chosen logic.
     *
     * The method considers the elevator's current state, direction, and travel time to choose
     * the most suitable elevator for the given request.
     *
     * @param request The request to be sent to the selected elevator.
     * @param elevatorsInfo Information about all elevators, including their current state,
     *                      floor, and direction, received from the ElevatorSubsystem.
     * @return The ID of the chosen elevator. Returns -1 if no suitable elevator is found.
     */
    public int selectElevator(Scheduler scheduler, ElevatorRequest request, String elevatorsInfo) {
        double speedPerFloor = Config.FLOORS_PER_SECOND;
        // Extracting elevator information from the string representation
        Pattern pattern = Pattern.compile("\\{elevatorId=(\\d+), currentState=(\\w+), currentFloor=(\\d+), currDirection=(\\w+)\\}");
        Matcher matcher = pattern.matcher(elevatorsInfo);

        // Initialize variables to track the chosen elevator and minimum travel time
        int selectedElevatorId = -1;
        int minTravelTimeSameDirection = Integer.MAX_VALUE;
        int minTravelTimeOppositeDirection = Integer.MAX_VALUE;

        // Iterate through each elevator's information
        while (matcher.find()) {
            try {
                // Extract relevant information from the matched groups
                int elevatorId = Integer.parseInt(matcher.group(1));
                Elevator.State currentState = Elevator.State.valueOf(matcher.group(2));
                int currentFloor = Integer.parseInt(matcher.group(3));
                ButtonDirection currDirection = ButtonDirection.valueOf(matcher.group(4));

                // Calculate the travel time for the current elevator
                int travelTime = calculateTravelTime(currentFloor, request.getFloorNumber(), speedPerFloor);

                // Check if the elevator is idle or moving in the requested direction
                if (currentState == Elevator.State.IDLE || (currDirection != ButtonDirection.NONE && currDirection == request.getButtonDirection())) {
                    // Update the selected elevator if it minimizes travel time
                    if (currDirection == request.getButtonDirection() && travelTime < minTravelTimeSameDirection) {
                        minTravelTimeSameDirection = travelTime;
                        selectedElevatorId = elevatorId;
                    } else if (currDirection != request.getButtonDirection() && travelTime < minTravelTimeOppositeDirection) {
                        minTravelTimeOppositeDirection = travelTime;
                        selectedElevatorId = elevatorId;
                    }
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing elevator information: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Selected elevator ID is : " + selectedElevatorId);
        return selectedElevatorId;
    }

    /**
     * Calculate the travel time from current floor to destination floor.
     * @param currentFloor The current floor.
     * @param destinationFloor The destination floor.
     * @param speedPerFloor The speed of the elevator per floor.
     * @return The travel time in seconds, or -1 if speedPerFloor is zero.
     */
    private int calculateTravelTime(int currentFloor, int destinationFloor, double speedPerFloor) {
        if (speedPerFloor == 0) {
            System.err.println("Error: Elevator is not moving. Speed per floor is zero.");
            return -1;
        }

        int floorsToTravel = Math.abs(destinationFloor - currentFloor);
        return (int) Math.ceil(floorsToTravel / speedPerFloor);
    }

}